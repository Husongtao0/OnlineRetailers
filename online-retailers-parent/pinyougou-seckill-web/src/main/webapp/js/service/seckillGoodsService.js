app.service('seckillGoodsService',function ($http) {
    this.findList=function () {
        return $http.get('/seckillGoods/findList.do');
    }

    this.findOne=function (id) {
        return $http.get('/seckillGoods/findOne.do?id='+id);
    }
    
    
    //下秒杀订单
    this.submitOrder=function (seckillId) {
        return $http.get('/seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
})