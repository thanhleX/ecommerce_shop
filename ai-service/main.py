import os
import mysql.connector
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import numpy as np
from typing import List, Optional

# Configuration
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "admin123",
    "database": "shop_db",
    "port": 3306
}

app = FastAPI(title="AI Support Chatbot Service")

# Enable CORS for React Frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Adjust this to ["http://localhost:5173"] for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load Model (MiniLM is small and fast for CPU)
print("Loading AI Model...")
model = SentenceTransformer('all-MiniLM-L6-v2')
print("Model loaded.")

# In-memory index for product embeddings
# In a real production app, use a Vector DB like ChromaDB or Pinecone
product_index = []

class ChatRequest(BaseModel):
    message: str

class ProductInfo(BaseModel):
    id: int
    name: str
    description: Optional[str]
    slug: str
    category: str

def fetch_products():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)
        
        query = """
            SELECT p.id, p.name, p.description, p.slug, c.name as category 
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.is_active = 1
        """
        cursor.execute(query)
        rows = cursor.fetchall()
        
        products = []
        for row in rows:
            products.append({
                "id": row["id"],
                "name": row["name"],
                "description": row["description"] or "",
                "slug": row["slug"],
                "category": row["category"]
            })
            
        cursor.close()
        conn.close()
        return products
    except Exception as e:
        print(f"Error fetching products: {e}")
        return []

def refresh_index():
    global product_index
    print("Refreshing product index...")
    products = fetch_products()
    
    if not products:
        print("No products found to index.")
        return
        
    # Create text to embed (Combine name, category, and description)
    texts = [f"{p['name']} ({p['category']}): {p['description']}" for p in products]
    embeddings = model.encode(texts)
    
    product_index = []
    for i, p in enumerate(products):
        product_index.append({
            "info": p,
            "embedding": embeddings[i]
        })
    print(f"Indexed {len(product_index)} products.")

@app.on_event("startup")
async def startup_event():
    refresh_index()

@app.post("/chat")
async def chat(request: ChatRequest):
    user_msg = request.message
    if not product_index:
        return {"reply": "Xin lỗi, hiện tại tôi không có thông tin sản phẩm nào. Vui lòng thử lại sau.", "products": []}

    # Compute query embedding
    query_embedding = model.encode([user_msg])[0]
    
    # Simple Cosine Similarity
    similarities = []
    for item in product_index:
        sim = np.dot(query_embedding, item["embedding"]) / (
            np.linalg.norm(query_embedding) * np.linalg.norm(item["embedding"])
        )
        similarities.append(sim)
    
    # Get top 3 matches
    top_indices = np.argsort(similarities)[-3:][::-1]
    results = []
    
    found_relevant = False
    for idx in top_indices:
        if similarities[idx] > 0.3:  # Similarity threshold
            results.append(product_index[idx]["info"])
            found_relevant = True
            
    if found_relevant:
        reply = "Tôi tìm thấy một số sản phẩm phù hợp với nhu cầu của bạn:"
    else:
        reply = "Tôi không tìm thấy sản phẩm nào chính xác như bạn mô tả, nhưng đây là một số gợi ý phổ biến cho bạn:"
        # Just return top 3 regardless if no high match, or fallback
        for idx in top_indices[:2]:
            results.append(product_index[idx]["info"])

    return {
        "reply": reply,
        "products": results
    }

@app.post("/reindex")
async def reindex():
    refresh_index()
    return {"status": "success", "count": len(product_index)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8081)
