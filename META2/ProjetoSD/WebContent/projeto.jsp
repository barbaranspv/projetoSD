<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
</style>
<body>
	<div class="row">
		<div class="col-sm-9 col-md-7 col-lg-5 mx-auto">
			<div class="card card-signin my-5">
				<div class="card-body">
					<c:choose>
						<c:when test="${session.loggedin == true}">
							<h1 class="card-title text-center">Bem-vindo! </h1>
							<h4 class="text-center"> ${session.notifica} </h4>
							<hr class="my-4">
							<c:choose>
								<c:when test="${session.admin == true}">
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="pesquisar" />">Efetuar Pesquisa<i class="fa fa-search ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="indexar" />">Indexar URL<i class="fa fa-paperclip ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="ligacoes" />">Ver ligações para um determinado URL<i class="fa fa-link ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="paineladmin" />">Ver painel de admin<i class="fa fa-key ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="verpesquisas" />">Ver histórico de pesquisas<i class="fa fa-history ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="daradmin" />">Dar privilégios de admin<i class="fas fa-id-badge ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="logout" />">Logout<i class="fa fa-sign-out ml-2" style="font-size:20px"></i></a></p>
								</c:when>
								<c:otherwise>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="pesquisar" />">Efetuar pesquisa<i class="fa fa-search ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="ligacoes" />">Ver ligaçõess para um determinado URL<i class="fa fa-link ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="verpesquisas" />">Ver histórico de pesquisas<i class="fa fa-history ml-2" style="font-size:20px"></i></a></p>
									<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="logout" />">Logout<i class="fa fa-sign-out ml-2" style="font-size:20px"></i></a></p>
								</c:otherwise>
							</c:choose>


						</c:when>
						<c:otherwise>
							<h1 class="card-title text-center">Welcome to ucBusca!<i class="fa fa-search ml-3" style="font-size:36px"></i></h1>
							<hr class="my-4">
							<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="login" />">Login</a></p>
							<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="register" />">Register</a></p>
							<p><a class="btn btn-lg btn-primary btn-block text-uppercase" href="<s:url action="pesquisar" />">Pesquisar</a></p>
							<hr>
							<p><a class="btn btn-lg btn-block text-uppercase" style="background-color: #3b5998; color: white" href="<s:url action="adfacebook" />"><i class="fab fa-facebook-f mr-2 "></i>Sign in or Login with Facebook</a></p>
						</c:otherwise>
					</c:choose>

				</div>
			</div>
		</div>
	</div>
</body>
</html>