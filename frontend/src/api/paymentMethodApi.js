import axiosClient from './axiosClient';

const paymentMethodApi = {
  getPaymentMethods() {
    return axiosClient.get('/payment-methods');
  }
};

export default paymentMethodApi;
