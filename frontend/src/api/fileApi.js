import axiosClient from './axiosClient';

const fileApi = {
  uploadFile: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    
    return axiosClient.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
};

export default fileApi;
