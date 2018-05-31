<html>
<body>
<#assign linkman="张三"  />

<#assign linkobejct={"id":1,"name":"lisi"}  />

<#assign itemList=[{"id":1,"name":"wagnwu"},{"id":2,"name":"赵六"}] />

<h1>hello ${linkman}</h1>
<br />
<#--展示设置对象的值-->
<h2>${linkobejct.id}</h2>
<h2>${linkobejct.name}</h2>

<#include "hello.ftl" />

<#--空值的处理-->
${key!"默认值"}

${key!}

<#if key??><#--<#if key?exists>-->
 如果条件成立 就出现
<#else >
 如果条件不成立就出现
</#if>

<br>
<!--遍历list-->
<table>
    <#list itemList as item>
        <#if item_index+1%2==1>
            //这是奇数
        <#else>
            //这是偶数
        </#if>
        <tr style="color: red">
            <td>${item_index+1}</td>
            <td>下标值：${item_index}</td>
            <td > id:${item.id}</td>
            <td>  name:${item.name}</td>
        </tr>
    </#list>
</table>
<br>
获取集合元素的大小：${itemList?size}

<br>

<#assign jsonstring="{'id':100000,'name':'张三'}" />
<#assign data = jsonstring?eval />
获取到的值：${data.id?c}

<br>
获取 日期：

输出仅仅是日期:${date?date}
输出仅仅是时间:${date?time}
输出日期和时间：${date?datetime}
自定义日期格式：${date?string("yyyy/MM/dd HH:mm:ss")}


<br>
运算符
<#assign x=10 />
<#if (x > 9)>
    <h2>这是x大于9的值</h2>
<#else >
    <h2>这是x小于9的值</h2>
</#if>
</body>
</html>
