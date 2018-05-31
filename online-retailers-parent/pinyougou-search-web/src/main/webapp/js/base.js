var app = angular.module('pinyougou', []);//定义模块

//过滤器  先定义一个过滤器  使用过滤器

app.filter('trustHtml',function ($sce) {
        return function (data) {//data就是原来的数据
          return $sce.trustAsHtml(data);
        }
})