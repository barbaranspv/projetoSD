<%--
  Created by IntelliJ IDEA.
  User: barba
  Date: 10/12/2019
  Time: 11:25
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <s:include value="include.jsp"/>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>UcBusca</title>
    </head>
    <style>
        body{
            background: rgb(2,0,36);
            background: linear-gradient(90deg, rgba(2,0,36,1) 0%, rgba(9,9,121,1) 35%, rgba(0,212,255,1) 100%);
        }
        form {
            position: absolute;
            top: 40%;
            left: 43%;
            background-color: transparent;
        }
        .input-field {
            position: relative;
            width: 250px;
            height: 44px;
            line-height: 44px;
            background-color: transparent;
        }
        label {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            color: #d3d3d3;
            transition: 0.2s all;
            cursor: text;
        }
        input {
            width: 100%;
            border: 0;
            outline: 0;
            padding: 0.5rem 0;
            border-bottom: 2px solid #d3d3d3;
            box-shadow: none;
            color: #111;
        }
        input:invalid {
            outline: 0;
            // color: #ff2300;
            //   border-color: #ff2300;
        }
        input:focus,
            input:valid {
            border-color: deepskyblue;
        }
        input:focus~label,
            input:valid~label {
            font-size: 14px;
            top: -24px;
            color: deepskyblue;
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
        <c:if test="${session.admin == true}">
            <s:form cssClass="form" action="indexar" method="post">
                <div class="input-field">
                    <s:textfield cssClass="input" type="text" id="name" name="url" required="true" style="background-color: transparent;"/>
                    <label for="name" style="background-color: transparent;">Qual o url que pretende indexar?</label>
                </div>
                <s:submit cssClass="example_b" href="add-website-here" target="_blank" rel="nofollow noopener" value="Adicionar URL"></s:submit>
            </s:form>
        </c:if>
        <c:if test="${session.admin == false}">
            <p id="texto">Não tem permissões para esta página.</p>
        </c:if>
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
</html>