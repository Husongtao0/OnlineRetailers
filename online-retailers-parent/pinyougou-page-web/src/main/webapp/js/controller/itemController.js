app.controller('itemController',function($http,$scope){
	$scope.num=1;
	
	$scope.addNum=function(x){
		if(isNaN(x)){
			return;
		}
		$scope.num=parseInt($scope.num);
		x=parseInt(x);
		
		$scope.num=$scope.num+x;
		if($scope.num<=1){
			$scope.num=1;
		}
	}
	
	//定义一个变量
	$scope.specificationItems={};
	//点击规格的时候调用的  去影响变量specificationItems
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
		
	}
	//判断被点击的规格是否在变量中存在，如果存在说明要显示样式
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	   /*var skuList=[
                    {"id":1369290, "title":"锤子手机不好 移动3G 16G","price":199,spec:{"网络":"移动3G","机身内存":"16G"},
                    {"id":1369291, "title":"锤子手机不好 移动3G 32G","price":123,spec:{"网络":"移动3G","机身内存":"32G"},
                    {"id":1369292, "title":"锤子手机不好 移动4G 16G","price":1231,spec:{"网络":"移动4G","机身内存":"16G"},
                    {"id":1369293, "title":"锤子手机不好 移动4G 32G","price":123123,spec:{"网络":"移动4G","机身内存":"32G"}
          ];*/

	
	$scope.sku=skuList[0];//这个对象就是 用于绑定页面显示某一个sku的数据的对象
	
	//** 加载默认的SKU的数据
	$scope.loadSku=function(){
		$scope.specificationItems=angular.fromJson(angular.toJson(skuList[0].spec));//深克隆
	}
	//当选择规格的时候调用这个方法 去影响￥scope.sku的变量的值   从skuList中获取。
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			//如果被点击的规格的数据  和遍历中的元素中的规格的数据一样，将该元素 赋值给Sku变量
			var skuObject = skuList[i];
			if(angular.toJson($scope.specificationItems)==angular.toJson(skuObject.spec)){
				$scope.sku=skuObject;
				break;
			}
		}
	}
	
	//添加购物车

	$scope.addCartToCartList=function(){
		$http.get('http://localhost:9107/cart/addCartToCartList.do?itemId='+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(
			function(response){
				if(response.success){
					window.location.href="http://localhost:9107/cart.html";
				}else{
					alert("添加失败");
				}
			}
		)
	}


	


	
	
	
	
	
	
});
