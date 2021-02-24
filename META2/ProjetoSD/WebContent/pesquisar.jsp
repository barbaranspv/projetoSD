<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <c:if test="${session.loggedin == true}">
        <s:include value="include.jsp"/>
    </c:if>
    <title>UcBusca</title>
</head>

<script>
    function searchToggle(obj, evt){
        var container = $(obj).closest('.search-wrapper');
        if(!container.hasClass('active')){
            container.addClass('active');
            evt.preventDefault();
        }
        else if(container.hasClass('active') && $(obj).closest('.input-holder').length == 0){
            container.removeClass('active');
            // clear input
            container.find('.search-input').val('');
        }
    }
</script>
<style>
body{
        background: rgb(2,0,36);
        background: linear-gradient(90deg, rgba(2,0,36,1) 0%, rgba(9,9,121,1) 35%, rgba(0,212,255,1) 100%);
}
html {
    font-family: sans-serif;
    -webkit-text-size-adjust: 100%;
    -ms-text-size-adjust: 100%;
}
body {
    margin: 0;
}
* {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
}
*:before,
*:after {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
}
/* Search */
.search-form {
    position: absolute;
    top: 5%;
    left: 45%;
    transform: translate(-50%);
}

.input-group.search-group {
    height: 50px;
    min-width: 50px;
    position: relative;
}

.input-group .form-control:hover,
.input-group .form-control:focus {
    padding: 10px 20px;
    width: 380px;
    color: #000;
    cursor: auto;
    background-color: #fff;
}

.input-group .form-control::-moz-placeholder {
    color: transparent;
}

.input-group .form-control::-webkit-input-placeholder {
    color: transparent;
}

.input-group .form-control:hover::-moz-placeholder,
.input-group .form-control:focus::-moz-placeholder {
    color: #ddd;
}
.input-group .form-control:hover::-webkit-input-placeholder,
.input-group .form-control:focus::-webkit-input-placeholder {
    color: #ddd;
}

.input-group .form-control:focus {
    box-shadow: 0px 1px 1px rgba(0, 0, 0, 0.075) inset;
    border-color: #e6e6e6;
}

.form-control.search-control {
    background: url(http://milansavov.com/img/zoom.svg) no-repeat right 11px center / 25px;
    border: 1px solid #ddd;
    border-radius: 5px;
    color: transparent;
    font-size: 16px;
    width: 50px;
    height: 50px;
    margin: 0;
    padding: 0;
    outline: 0 none;
    position: absolute;
    right: 0;
    top: 0;
    z-index: 10;
    cursor: pointer;
    -webkit-backface-visibility: hidden;

    transition: width 0.25s;
}
.head {
    font-size: 32px;
    position: absolute;
    top: -20%;
    left: 120%;
}
 .container {
     display: inline-block;
     cursor: pointer;
 }

.bar1, .bar2, .bar3 {
    width: 35px;
    height: 5px;
    background-color: white;
    margin: 6px 0;
    transition: 0.4s;
}

.change .bar1 {
    -webkit-transform: rotate(-45deg) translate(-9px, 6px);
    transform: rotate(-45deg) translate(-9px, 6px);
}

.change .bar2 {opacity: 0;}

.change .bar3 {
    -webkit-transform: rotate(45deg) translate(-8px, -8px);
    transform: rotate(45deg) translate(-8px, -8px);
}
</style>
<body theme="simple">

    <c:if test="${session.loggedin == true}">
        <a class="container" href="<s:url action="projeto2"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
    </c:if>
    <c:if test="${session.loggedin == false}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
    </c:if>
    <c:if test="${session.loggedin == null}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
    </c:if>

    <script>
        function myFunction(x) {
            x.classList.toggle("change");
        }
    </script>
    <s:form cssClass="search-form" action="pesquisar" method="post">
        <div class="input-group search-group">
            <s:textfield cssClass="form-control search-control" type="text" name="pesquisa" class="form-control search-control" placeholder="Enter your search..."/>
            <h1 class="head" style="color: white">UcBusca</h1>
            <s:submit style="display: none;"/>

        </div><!-- /.input-group -->
    </s:form><!-- /.search-form -->

    <style>

        .card {
            /* Add shadows to create the "card" effect */
            box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
            transition: 0.3s;
        }

        /* On mouse-over, add a deeper shadow */
        .card:hover {
            box-shadow: 0 8px 16px 0 rgba(0,0,0,0.2);
        }

        /* Add some padding inside the card container */
        .container {
            padding: 2px 16px;
        }
        .example_b {
            color: #fff !important;
            text-transform: uppercase;
            text-decoration: none;
            background: #60a3bc;
            padding: 20px;
            border-radius: 50px;
            display: inline-block;
            border: none;
            transition: all 0.4s ease 0s;
        }
        .example_b:hover {
            text-shadow: 0px 0px 6px rgba(255, 255, 255, 1);
            -webkit-box-shadow: 0px 5px 40px -10px rgba(0,0,0,0.57);
            -moz-box-shadow: 0px 5px 40px -10px rgba(0,0,0,0.57);
            transition: all 0.4s ease 0s;
        }
    </style>
    <div style="margin-top:5%; margin-left: 2%; margin-right: 2%;">
        <div style="position: absolute; top:13%; right:36%">
            <a class="example_b" href="<s:url action="traduz"/>" rel="nofollow noopener">Traduzir Resultados</a>
            <a class="example_b" href="${shareUrl}" rel="nofollow noopener">Partilhar Resultados</a>
        </div>
        <c:forEach items="${info}" var="pesquisa">
            <div class="card">
                <div class="container" style="background: white; border-radius: 25px; margin-bottom: 2%">
                    <c:forEach items="${pesquisa}" var="item" varStatus="loop">
                        <c:if test="${loop.index == 0}">
                            <p style="font-weight: bold"><c:out value="${item}" /><br></p>
                        </c:if>
                        <c:if test="${loop.index == 1}">
                            <a href="${item}"><p><c:out value="${item}" /><br></p></a>
                        </c:if>
                        <c:if test="${loop.index == 3}">
                            <p>Número de ligações para a página: <c:out value="${item}" /><br></p>
                        </c:if>
                        <c:if test="${loop.index != 0 && loop.index != 1 && loop.index != 3}">
                            <p><c:out value="${item}" /><br></p>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>

    </div>

</body>
</html>
