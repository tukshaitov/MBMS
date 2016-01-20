<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>MBMS</title>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ext-css/ext-all.css"/>" />
<style type="text/css">
	#msg-div {
		position: absolute;
		left: 50%;
		top: 10px;
		width: 400px;
		margin-left: -200px;
		z-index: 20000;
	}
	#msg-div .msg {
		border-radius: 8px;
		-moz-border-radius: 8px;
		background: #F6F6F6;
		border: 2px solid #ccc;
		margin-top: 2px;
		padding: 10px 15px;
		color: #555;
	}
	#msg-div .msg h3 {
		margin: 0 0 8px;
		font-weight: bold;
		font-size: 15px;
	}
	#msg-div .msg p {
		margin: 0;
	}
</style>

<script type="text/javascript">
	var pageContext = '${pageContext.request.contextPath}/';
</script>
<script type="text/javascript" src="<c:url value="/resources/js/ext-all-debug.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/i18n/login.js"/>"></script>

</head>
<body>
</body>
</html>
