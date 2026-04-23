package com.example.shop.application.service;

import com.example.shop.application.dto.request.BlogRequest;
import com.example.shop.application.dto.response.BlogResponse;
import com.example.shop.application.mapper.BlogMapper;
import com.example.shop.domain.entity.Blog;
import com.example.shop.domain.entity.BlogCategory;
import com.example.shop.domain.entity.User;
import com.example.shop.domain.exception.AppException;
import com.example.shop.domain.exception.ErrorCode;
import com.example.shop.domain.repository.BlogCategoryRepository;
import com.example.shop.domain.repository.BlogRepository;
import com.example.shop.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final UserRepository userRepository;
    private final BlogMapper blogMapper;

    // Public: chỉ bài đã published
    public Page<BlogResponse> getPublishedBlogs(Pageable pageable) {
        return blogRepository.findByIsPublishedTrue(pageable).map(blogMapper::toResponse);
    }

    public Page<BlogResponse> getBlogsByCategorySlug(String categorySlug, Pageable pageable) {
        return blogRepository.findByBlogCategorySlugAndIsPublishedTrue(categorySlug, pageable)
                .map(blogMapper::toResponse);
    }

    public List<BlogResponse> getFeaturedBlogs() {
        return blogRepository.findByIsPublishedTrueAndIsFeaturedTrueOrderByCarouselOrderAsc()
                .stream()
                .limit(4)
                .map(blogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BlogResponse getBlogBySlug(String slug) {
        Blog blog = blogRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        return blogMapper.toResponse(blog);
    }

    // Admin: tất cả blogs
    public Page<BlogResponse> getAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable).map(blogMapper::toResponse);
    }

    @Transactional
    public BlogResponse create(String username, BlogRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Blog blog = blogMapper.toEntity(request);
        blog.setUser(user);

        if (request.getBlogCategoryId() != null) {
            BlogCategory category = blogCategoryRepository.findById(request.getBlogCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
            blog.setBlogCategory(category);
        }

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Transactional
    public BlogResponse update(Long id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        blog.setTitle(request.getTitle());
        blog.setSlug(request.getSlug());
        blog.setContent(request.getContent());
        blog.setThumbnail(request.getThumbnail());
        blog.setIsPublished(request.getIsPublished());
        blog.setIsFeatured(request.getIsFeatured());
        blog.setCarouselOrder(request.getCarouselOrder());

        if (request.getBlogCategoryId() != null) {
            BlogCategory category = blogCategoryRepository.findById(request.getBlogCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
            blog.setBlogCategory(category);
        } else {
            blog.setBlogCategory(null);
        }

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Transactional
    public void delete(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        blogRepository.delete(blog);
    }
}
