<%@ page import="ru.home.domain.Auto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Детали товара</title>
    <%@ include file="bootstrap.jsp" %>
</head>

<body>
<div class="container">
    <div class="row">
        <%--если есть авто, то показываем--%>
        <% if (request.getAttribute("item") != null) { %>
        <% Auto item = (Auto) request.getAttribute("item"); %>
        <div class="col-sm-4" style="width: auto">
            <%--показываем авто и его свойства--%>
            <div class="card">
                <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top" >
                <div class="card-body">
                    <small>Название:</small> <h5 class="card-title"><%= item.getName() %></h5>
                    <small>Описание:</small> <p class="card-text"><%= item.getDescription()%></p>
                </div>
            </div>
        </div>
        <%--изменяем авто--%>
        <div class="col-3">
            <div class="dropdown">
                <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Изменить
                </button>
                <%--форма--%>
                <div class="dropdown-menu">
                    <form class="px-4 py-3" action="<%= request.getContextPath() %>/details/<%= item.getId() %>" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="autoName">Название</label>
                            <input type="text" class="form-control" id="autoName" name="autoName" placeholder="<%= item.getName() %>">
                        </div>
                        <div class="form-group">
                            <label for="autoDescription">Описание</label>
                            <input type="text" class="form-control" id="autoDescription" name="autoDescription" placeholder="<%= item.getDescription()%>">
                        </div>
                        <div class="form-group">
                            <label for="autoFile">Изображение</label>
                            <input type="file" class="form-control" id="autoFile" name="autoFile" accept="image/*">
                        </div>

                        <button type="submit" class="btn btn-primary">Изменить</button>
                    </form>
                </div>
            </div>
            <form action="<%= request.getContextPath() %>/details/<%= item.getId() %>" method="post" enctype="multipart/form-data">
                <button class="btn btn-danger" type="submit" id="delete" name="delete">Удалить</button>
            </form>

            <form action="<%= request.getContextPath() %>/details/<%= item.getId() %>" method="post" enctype="multipart/form-data">
                <button type="submit" id="load" name="load">Load</button>
            </form>
        </div>
        <% } %>
    </div>
</div>

</body>
</html>