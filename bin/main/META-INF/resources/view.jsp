<%@ include file="/init.jsp" %>

<p>
	<b><liferay-ui:message key="apitraining.caption"/></b>
</p>

<portlet:actionURL name="createSomething" var="createSomethingUrl">
    <portlet:param name="myParam" value="my value" />
</portlet:actionURL>

<form action="${createSomethingUrl}" method="post">
	Name: <input type="text" name="<portlet:namespace/>api_name" id="api_name" />&nbsp;&nbsp;
	Age: <input type="text" name="<portlet:namespace/>api_age" id="api_age" />
	<input type="submit" name="api_submit" />
</form>

<div id='bg_api'>

	<table>
		<c:forEach items="${requestScope.api_somethings}" var="something">
			<tr>
				<td>Something Name: <c:out value="${something.name}"/></td>
	        	<td>Something Age: <c:out value="${something.age}"/></td>  
	    	</tr>
		</c:forEach>
	</table>
</div>