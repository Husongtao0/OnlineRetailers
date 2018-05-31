 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){

        var id = $location.search()['id'];
        if(id==null || id==undefined){
        	return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				//将查询到的商品的介绍的数据 存入到kindeditor
                editor.html($scope.entity.goodsDesc.introduction);

                //将图片的字符串 改成JSON对象
                $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);
                for (var i=0;i<$scope.entity.itemList.length;i++){
                	//转成JSON对象
                    $scope.entity.itemList[i].spec=angular.fromJson( $scope.entity.itemList[i].spec);
				}

			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		//这个先取出来数据
        $scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
                    $scope.entity={};//清空
                    editor.html('');//清空富文本编辑器中的内容。
					//调回到商家的后台商品的列表页面
					window.location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//添加商品
	// $scope.add=function () {
	// 	//先获取到富文本编辑器中的介绍数据 绑定到变量中entity.goodsDesc.introduction
	// 	$scope.entity.goodsDesc.introduction=editor.html();
     //    goodsService.add( $scope.entity).success(
     //    	function (response) {//result
	// 			if(response.success){
	// 				$scope.entity={};//清空
     //                editor.html('');//清空富文本编辑器中的内容。
	// 			}else{
	// 				alert("添加失败");
	// 			}
     //        }
	// 	)
    // }
    
    //上传图片
	
    $scope.uploadFile=function () {
        uploadService.uploadFile().success(
        	function (response) {//result :包含里面的图片的URL
				if(response.success){
                    $scope.image_entity.url=response.message;
				}else{
					alert("失败");
				}

            }
		)
    }

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
    /**
	 * 将图片对象添加到数组中
     */
    $scope.add_image_entity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


	
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	
	//查询一级分类的列表
	$scope.selectCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {//List<tbitemcat>
				$scope.itemCat1List=response;
            }
		)
    }

    /**
	 * 监控一级分类的id的值的变化，触发以下逻辑   根据一级分类的ID 查询二级分类的列表展示
     */
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {

    	if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemcat>
                    $scope.itemCat2List=response;
                }
            )
		}
    })


    /**
     * 监控二级级分类的id的值的变化，触发以下逻辑 根据二级分类的ID 查询三级分类的列表展示
     */
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {

        if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemcat>
                    $scope.itemCat3List=response;
                }
            )
        }
    })

    /**
     * 监控三级分类的id的值的变化，触发以下逻辑 根据三级分类的ID 自己的对象的数据 获取到模板的ID 展示到页面
     */
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {

        if(newValue!=undefined){
            itemCatService.findOne(newValue).success(
            	function (response) {//tbitemcat
					$scope.entity.goods.typeTemplateId=response.typeId;//展示模板的id
                }
			);
        }
    })

    /**
	 * 监控模板的ID的值的变化 ，查询模板的对象，查询关联到的品牌的列表 展示到下拉框中
     */
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

        if(newValue!=undefined){
        	//查询模板的数据 查询品牌 和扩展属性的展示
           typeTemplateService.findOne(newValue).success(
           		function (response) {//TbTypeTemplate
					$scope.typeTemplate=response;
                    $scope.typeTemplate.brandIds=angular.fromJson($scope.typeTemplate.brandIds);
                    if($location.search()['id']!=null || $location.search()['id']!=undefined ){
						//这个表示要编辑   不需要从模板中获取扩展属性
					}else{
                        $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.typeTemplate.customAttributeItems);
					}

                }
		   )
			//查询的模板对应的规格列表
			typeTemplateService.findSpecList(newValue).success(
				function (response) {//response就是List<map>
					$scope.specList = response;
                }
			)

        }
    })

    /**
	 * [{"attributeName":"网络制式","attributeValue":["移动3G"]}]
	 * 当点击复选框的时候调用去影响变量：$scope.entity.goodsDesc.specificationItems的值
	 * specName：就是你点击的选项所对应的规格名称  网路
	 * specValue:就是你点击的选项的值   4G
     */
	$scope.updateSpecAttribute=function ($event,specName,specValue) {
        var object = $scope.searchObjectByKey( $scope.entity.goodsDesc.specificationItems,'attributeName',specName);

		if(object!=null){
            //如果有对象
			if($event.target.checked){
                object.attributeValue.push(specValue);
			}else{
                object.attributeValue.splice(object.attributeValue.indexOf(specValue),1);//

				// 判断这个对象中的数组属性attributeValue的长度如果是0 删除该对象
				if( object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else {
            //如果没有对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specValue]});
		}
    }

    /**
	 * 重新构建SKU列表  深克隆
     */
    $scope.createItemList=function () {
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];

        //循环遍历$scope.entity.goodsDesc.specificationItems ---》 [{"attributeName":"网络制式","attributeValue":["移动3G"]}]

		var items = $scope.entity.goodsDesc.specificationItems;

		for (var i=0;i<items.length;i++){
			var object = items[i];
            $scope.entity.itemList=addColumn($scope.entity.itemList,object.attributeName,object.attributeValue);
		}
    }

    /**
	 *
     * @param list   [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
     * @param column 网络制式
     * @param clumnValues ["移动3G"]
     * @returns {Array}
     */
    addColumn=function (list,column,clumnValues) {
		var newList = [];
		for(var i=0;i<list.length;i++){
          var oldRow =  list[i];//{spec:{'网路制式'“：3g},price:0,num:9999,status:'0',isDefault:'0'}
			for (var j = 0;j<clumnValues.length;j++){
                var newRow = angular.fromJson(angular.toJson(oldRow));
                newRow.spec[column]=clumnValues[j];//加规格属性
                newList.push(newRow);
			}
		}
    	return newList;
    }


    //
	$scope.status=['未审核','已审核','审核未通过','关闭'];


    $scope.itemCatList=[];

    //首先 从数据库查询所有的商品分类----》   $scope.itemCatList[1]='手机'，$scope.itemCatList[865]='电脑'，页面做：----{{itemCatList[]}}

	$scope.findItemCatList=function () {
        itemCatService.findAll().success(
        	function (response) {//List<itemCat>
				for(var i =0;i<response.length;i++){
                    var itemcat = response[i];
                    $scope.itemCatList[itemcat.id]=itemcat.name;
                }
            }
		)
    }


    /**
	 *
     * @param specName  网络
     * @param specValue  移动3G
     * @returns {boolean}
     */
    $scope.checkAttributeValue=function (specName,specValue) {
		//实现判断的逻辑
		//在从数据库中查询到的被勾选的数据的数组中判断 展示的列表中的选项是否存在 ，如果存在说明 是应该被勾选的
		var specificationItems = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(specificationItems,'attributeName',specName);

		if(object!=null){
            if(object.attributeValue.indexOf(specValue)!=-1){//说明找到
            	return true;
			}else{
            	return false;
			}
		}else{
			return false;
		}

    }



    
});	
