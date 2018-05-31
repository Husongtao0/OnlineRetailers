app.controller('cartController',function ($scope,$http,cartService,addressService) {
    
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {//List<cart>    [{sellerId:1,orderItems:[{price,num,totalFee}]},{}]
                $scope.cartList=response;
                //统计总金额 已经总件数
               $scope.sum();
            }
        )
    }

    /**
     * 要添加商品到购物车中（添加 和移除）
     * @param itemId 要添加的商品的ID
     * @param num  要添加的数量  数量可以是负数   表示移除
     */
    $scope.addCartToCartList=function (itemId,num) {
        cartService.addCartToCartList(itemId,num).success(
            function (response) {//result
                if(response.success){
                    $scope.findCartList();//刷新购物车的列表
                }
            }
        )
    }

    $scope.sum=function () {
        $scope.money=0;//总金额
        $scope.totalNum=0;//总件数


        for(var i=0;i<$scope.cartList.length;i++){
            var cart = $scope.cartList[i];
            var orderItemList = cart.orderItemList;

            for(var j =0;j<orderItemList.length;j++){
                console.log(orderItemList[j]);
                $scope.money+=orderItemList[j].totalFee;
                $scope.totalNum+=orderItemList[j].num;

            }


        }

    }
    
    $scope.findAdressList=function () {
        addressService.findAddressList().success(
            function (response) {//List<tbaddress>
                $scope.addressList=response;

                for (var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        //找到的默认的地址的对象
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }


            }
        )
    }

    //表示当前的地址对象
   // $scope.address={};

    //选中地址的方法
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }
    
    $scope.isSelectedAddress=function (address) {
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    //选中支付类型
    $scope.order={paymentType:"1",sourceType:"2"};
    
    $scope.selectPayType=function (paymentType) {
        $scope.order.paymentType=paymentType;
    }
    
    //提交订单
    $scope.submitOrder=function () {
        //需要将地址的值 绑定到order变量中
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        $http.post('/order/add.do',$scope.order).success(
            function (response) {//result
                if(response.success){
                    //跳转到支付的页面
                   window.location.href="pay.html";
                }else{
                    alert("错误")
                }
            }
        )
    }

    
})