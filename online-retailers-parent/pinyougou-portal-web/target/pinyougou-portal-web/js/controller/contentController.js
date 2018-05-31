 //控制层 
app.controller('contentController' ,function($scope,$controller,contentService){
	
	// $controller('baseController',{$scope:$scope});//继承

    $scope.contentList=[];
	$scope.findContentListByCategoryId=function (categoryId) {
        contentService.findContentListByCategoryId(categoryId).success(
        	function (response) {//List<tbcontent>  是一部分的数据（广告轮播的数据）
				$scope.contentList[categoryId]=response;
            }
		)
    }
    
    
    $scope.search=function () {
        var keywords = $scope.keywords;
        window.location.href="http://localhost:9104/search.html#?keywords="+keywords;
    }
    
});	
