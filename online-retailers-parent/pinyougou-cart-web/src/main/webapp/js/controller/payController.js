app.controller('payController',function ($scope,$location,payService) {

    //写一个方法 在页面初始化的时候调用  发送请求 生成二维码 ，展示二维码

    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {//Map  (金额  url  交易订单号)

                $scope.total_fee=(response.total_fee/100).toFixed(2);//金额
                $scope.out_trade_no=response.out_trade_no;//交易订单号码

                //生成二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                //生成了二维码之后就应该调用这个方法：

                $scope.queryStatus();


            }
        )
    }
    //方法就是调用接口 查询支付的状态
    $scope.queryStatus=function () {
        payService.queryStatus($scope.out_trade_no).success(
            function (response) {//Result
                if(response.success){//支付成功
                    window.location.href="paysuccess.html#?money="+$scope.total_fee;
                }else{
                    //支付失败有很多种 ，其中有一种是超时
                    if(response.message=='支付超时'){
                        //重新生成二维码。
                        $scope.createNative();
                    }else{
                        window.location.href="payfail.html";
                    }

                }
            }
        )
    }


    $scope.getMoney=function () {
        return $location.search()['money'];
    }

})