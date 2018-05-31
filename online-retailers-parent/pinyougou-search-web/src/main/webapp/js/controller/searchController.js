app.controller('searchController',function($scope,$location,searchService){
    //绑定了一个变量
    $scope.searchMap={'keywords':'','category':'','brand':'',spec:{},'price':'','sortField':'','sortType':'','pageNo':1,'pageSize':40};
    //搜索
    $scope.search=function(){
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果

                //构建分页的标签  1  2 3 4
                buildPageLable();
            }
        );
    }
    
    $scope.initSearchMap=function () {
        $scope.searchMap={'keywords':$scope.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','sortField':'','sortType':'','pageNo':1,'pageSize':40};
    }

    //显示以当前的页码为中心的 5 页
    buildPageLable=function () {
        //循环遍历 总页数
        var totalPages = $scope.resultMap.totalPages;
        $scope.pageLabel=[];

        var firstPage=1;//开始的页码
        var lastPage=$scope.resultMap.totalPages;

        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点


        if($scope.resultMap.totalPages>5){
            if($scope.searchMap.pageNo<=3){
                firstPage=1;
                lastPage=5;//显示前5页
                $scope.firstDot=false;
                $scope.lastDot=true;
            }else if($scope.searchMap.pageNo>$scope.resultMap.totalPages-2){
                lastPage=$scope.resultMap.totalPages;
                firstPage=$scope.resultMap.totalPages-4;
                $scope.firstDot=true;
                $scope.lastDot=false;

            }else{
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
                $scope.firstDot=true;
                $scope.lastDot=true;
            }
        }else{
            alert("总页数小于=5页，应该显示全部的页码 前 5页")

            var firstPage=1;//开始的页码
            var lastPage=$scope.resultMap.totalPages;

            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }

    /**
     * 根据当前的页码来分页查询  传过去的当前的页码就是参数
     */
    $scope.searchByPage =function(pageNo){
        //先判断是否为数字
        //console.info(isNaN(pageNo));//如果返回的是false 就是数字 如果返回的是true 就是字符串
        if(isNaN(pageNo)){
            alert("你丫的输入的不是数字");
            return ;
        }
        pageNo=parseInt(pageNo);//转换成数字
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    /**
     * 在点击分类 或者品牌 或者规格的时候被调用
     * key：category
     * value:手机
     */
    $scope.addSearchItem=function (key,value) {

        //添加普通的搜索项
        if(key=='category'|| key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{
            //添加的是规格的搜索项  spec:{"网络":"移动3G"}
            $scope.searchMap.spec[key]=value;
        }

        $scope.search();



    }
    //移除搜索项
    $scope.removeSearchItem=function (key) {

        //添加普通的搜索项
        if(key=='category'|| key=='brand' || key=='price'){
            $scope.searchMap[key]='';
        }else{
            //添加的是规格的搜索项  spec:{"网络":"移动3G"}
            delete $scope.searchMap.spec[key];//删除对象中的属性
        }
        $scope.search();
    }

    $scope.sortSearch=function (sortField,sortType) {
        //需要改变 sortField和sortType的值
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sortType=sortType;
        $scope.search();
    }

    //判断搜索的关键字是否包含品牌的名称 如果有那么直接显示该品牌，而且隐藏面板
    $scope.keywordsIsBrand=function () {
        //  应该遍历 品牌列表   然后 对比，如果品牌列表中的字符 在关键字中出现，就应该说明是包含品牌的，然后返回true ，否则返回false
        var brandList = $scope.resultMap.brandList;//数组
        for (var i=0;i<brandList.length;i++){
            if($scope.searchMap.keywords.indexOf(brandList[i].text)!=-1){
                $scope.searchMap.brand=brandList[i].text;//把对应品牌赋值给搜索的条件项，条件项中如果有值就显示出来了
                return true;
            }
        }
        return false;
    }

    /**
     * 页面一旦被加载就应该被调用 目的是为了从首页获取到搜索的关键字并且完成搜索的功能 从索引库中获取数据展示
     */
    $scope.loadKeywords=function () {
        var keywords = $location.search()['keywords'];
        if(keywords==null || keywords==undefined){
            return;
        }
        $scope.searchMap.keywords=keywords;//关键字有值  查询
        $scope.search();
    }


});
