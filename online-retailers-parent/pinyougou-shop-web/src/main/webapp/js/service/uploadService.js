app.service("uploadService",function($http){

    this.uploadFile=function(){
        var formData =new FormData();//构建一个formData对象(表单)
        //参数1：file就是和后台controller.java中的参数名要一致
        //值：file  是 文件类型type为file对应的标签所对应的id的值  document.getElementById("file")
        //files[0] 获取第一个文件对象
        formData.append("file",file.files[0]);//file类型的数据  key=1  --->controller key

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},//默认的请情况下：angurljs 的类型就是application/json;默认的情况下会使用文件上传的数据类型而且多了个一些分隔符
            transformRequest: angular.identity//表示使用angulr的定义 定义request传输的数据格式。
        });
    }
});
