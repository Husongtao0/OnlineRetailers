 //控制层 
app.controller('userController' ,function($scope,userService){

	$scope.entity={};
    /**
	 * 注册用户
     */
	$scope.register=function () {
		//1.js的校验
		if($scope.entity.username==null || $scope.entity.username==undefined){
			alert("用户名不能为空");
			return;
		}

		//2.密码两次都应该相同
		if($scope.entity.password!=$scope.confirmPassword){
            alert("密码不一致");
			return;
		}
        userService.add($scope.entity,$scope.smsCode).success(
        	function (response) {//result
				if(response.success){
					//跳转到用户的中心的页面中去
					window.location.href="home-index.html";//http://localhost:9106/home-index.html
				}else{
					alert(response.message);
				}
            }
		)
    }

    /**
	 * 在点击发送短信验证码的时候调用
     */
    $scope.sendSms=function () {
		userService.sendSms($scope.entity.phone).success(
			function (response) {
				// if(response.success){
					alert(response.message);
				// }
            }
		)
    }


    
});	
