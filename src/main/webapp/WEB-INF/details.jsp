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
            <div class="col-sm-8" style="width: auto">
                <%--показываем авто и его свойства--%>
                <div class="card">
                    <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top img-thumbnail" >
                    <div class="card-body ml-1">
                        <small>Название:</small> <h5 class="card-title"><%= item.getName() %></h5>
                        <small>Описание:</small> <p class="card-text"><%= item.getDescription()%></p>
                        <small>Год выпуска:</small> <p class="card-text"><%= item.getYear()%></p>
                        <small>Мощность:</small> <p class="card-text"><%= item.getPower()%></p>
                        <small>Цвет:</small> <p class="card-text"><%= item.getColor()%></p>
                    </div>
                </div>
            </div>
            <%--изменяем авто--%>
            <div class="col-sm-4">
                <div class="btn-group-vertical m-1">
                    <div class="dropdown mb-1">
                        <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Изменить
                        </button>
                        <%--форма--%>
                        <div class="dropdown-menu">
                            <form class="p-1" action="<%= request.getContextPath() %>/details/<%= item.getId() %>" method="post" enctype="multipart/form-data">
                                <div class="form-group">
                                    <label for="autoName">Название</label>
                                    <input type="text" class="form-control" id="autoName" name="autoName" placeholder="<%= item.getName() %>">
                                </div>
                                <div class="form-group">
                                    <label for="autoDescription">Описание</label>
                                    <input type="text" class="form-control" id="autoDescription" name="autoDescription" placeholder="<%= item.getDescription()%>">
                                </div>
                                <div class="form-group">
                                    <label for="autoYear">Год выпуска</label>
                                    <input type="number" step="1" min="2018" class="form-control" id="autoYear" name="autoYear" placeholder="<%= item.getYear()%>">
                                </div>
                                <div class="form-group">
                                    <label for="autoPower">Мощность</label>
                                    <input type="number" step="0.1" min="0.5" class="form-control" id="autoPower" name="autoPower" placeholder="<%= item.getPower()%>">
                                </div>
                                <div class="form-group">
                                    <label for="autoColor">Цвет</label>
                                    <input type="text" class="form-control" id="autoColor" name="autoColor" placeholder="<%= item.getColor()%>">
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
                        <button class="btn btn-danger mb-1" type="submit" id="delete" name="delete">Удалить</button>
                    </form>

                    <form action="<%= request.getContextPath() %>/details/<%= item.getId() %>" method="post" enctype="multipart/form-data">
                        <button class="btn btn-secondary" type="submit" id="load" name="load">Сохранить</button>
                    </form>
                </div>
            </div>
            <% } %>
    </div>
</div>

</body>
</html>