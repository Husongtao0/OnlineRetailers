app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    this.addCartToCartList=function (itemId,num) {
        return $http.get('cart/addCartToCartList.do?itemId='+itemId+"&num="+num);
    }
});
