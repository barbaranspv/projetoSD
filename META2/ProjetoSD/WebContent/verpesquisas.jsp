<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <s:include value="include.jsp"/>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://kit.fontawesome.com/f77f0b423e.js" crossorigin="anonymous"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>UcBusca</title>
</head>
<style>
    body{
        background: rgb(2,0,36);
        background: linear-gradient(90deg, rgba(2,0,36,1) 0%, rgba(9,9,121,1) 35%, rgba(0,212,255,1) 100%);
    }
    .card-signin {
        border: 0;
        border-radius: 1rem;
        box-shadow: 0 0.5rem 1rem 0 rgba(0, 0, 0, 0.1);
    }

    .card-signin .card-title {
        margin-bottom: 2rem;
        font-weight: 500;
        font-size: 1.5rem;
        font-weight: bold;
    }

    .card-signin .card-body {
        padding: 2rem;
    }

    .card-signin .btn{
        font-size: 80%;
        border-radius: 5rem;
        letter-spacing: .1rem;
        font-weight: bold;
        padding: 1rem;
        transition: all 0.2s;
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
    #texto{
        color: white;
        text-align: center;
    }
</style>
<body>
    <c:if test="${session.loggedin == true}">
        <a class="container" href="<s:url action="projeto2"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
        <div class="row">
            <div class="col-sm-9 col-md-7 col-lg-5 mx-auto">
                <div class="card card-signin my-5">
                    <div class="card-body text-center">
                        <c:forEach items="${info}" var="item" varStatus="loop">
                            <c:if test="${loop.index == 0}">
                                <h1 class="card-title text-center"><c:out value="${item}" /><br></h1>
                            </c:if>
                            <c:if test="${loop.index != 0}">
                                <c:if test="${item != 'null'}">
                                    <p style="text-align: center"><c:out value="${item}" /><br></p>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <c:if test="${session.loggedin == false}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
        <p id="texto">Não tem permissões para esta página.</p>
    </c:if>
    <c:if test="${session.loggedin == null}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
        <p id="texto">Não tem permissões para esta página.</p>
    </c:if>


</body>
<!--
<body>
<c:forEach items="${info}" var="item" varStatus="loop">

    <p><c:out value="${item}" /><br></p>

</c:forEach>
</body>
-->
</html>
