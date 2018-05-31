app.controller('seckillGoodsController',function ($scope,$location,$interval,seckillGoodsService) {
    
    //查询所有的秒杀商品的列表
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {//List
                $scope.list=response;
            }
        )
    }

    //根据商品的ID 查询商品
    
    $scope.findOne=function (id) {
        //获取商品的ID
        var id = $location.search()['id'];

        seckillGoodsService.findOne(id).success(
            function (response) {//respone就是商品对象
                $scope.entity=response;
                //获取商品对象中的结束时间 - 当前的时间  =  剩余的时间（倒计时）

                var endTime = new Date(response.endTime).getTime();//获取秒杀结束时间的毫秒数（距离1970到endTime时间点的毫秒数）
                var currnetTime = new Date().getTime();//当前时间的毫秒数（距离1970到现在时间）
                var allsecond =  Math.floor(((endTime-currnetTime)/1000));


                //$scope.second = 10;
                time= $interval(function(){
                    $scope.timeString =   convertTimeString(allsecond);
                    if(allsecond>0){
                        allsecond =allsecond-1;
                    }else{
                        $interval.cancel(time);
                        alert("秒杀服务已结束");
                    }
                },1000);


            }
        )
    }


    //转换秒为   天小时分钟秒格式  XXX天 10:22:33
    convertTimeString=function(allsecond){
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数

        if(days>0){
            days=days+"天 ";
        }
        if(hours<10){
            hours="0"+hours;
        }
        if(minutes<10){
            minutes="0"+minutes;
        }
        if(seconds<10){
            seconds="0"+seconds;
        }
        return days+hours+":"+minutes+":"+seconds;
    }

    //提交订单
    $scope.submitOrder=function (seckillId) {
        seckillGoodsService.submitOrder(seckillId).success(
            function (response) {//response是一个Result
                if(response.success){
                    //跳转支付页面
                    window.location.href="pay.html";
                }else{
                    alert(response.message);
                }

            }
        )
    }





})