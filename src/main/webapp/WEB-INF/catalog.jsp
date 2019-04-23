<%@ page import="ru.home.domain.Auto" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Каталог</title>
    <%@ include file="bootstrap.jsp" %>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col">
            <h1>Каталог</h1>

            <form class="mt-3" action="<%= request.getContextPath() %>/catalog" method="post" name="searchForm">

                <div class="btn-group" role="group">
                    <%-- выпадающий список --%>
                    <select class="custom-select" name="inputSelect">
                        <option selected value="search-name">Название</option>
                        <option value="search-description">Описание</option>
                    </select>

                    <%-- строка поиска --%>
                    <input name="query" class="form-control" type="search" placeholder="Поиск">
                </div>
            </form>


            <div class="row">
                <% if (request.getAttribute("items") != null) { %>
                <% for (Auto item : (List<Auto>)request.getAttribute("items")) { %>
                <div class="col-sm-4 mt-3" style="width: auto">
                    <div class="card">
                        <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top">
                        <div class="card-body">
                            <small>Название:</small> <h5 class="card-title"><%= item.getName() %></h5>
                            <small>Описание:</small> <p class="card-text"><%= item.getDescription()%></p>
                            <a href="<%= request.getContextPath() %>/details/<%= item.getId() %>" class="btn btn-primary">Детали</a>
                        </div>
                    </div>
                </div>
                <% } %>
                <% } %>
            </div>


            <form class="mt-3" action="<%= request.getContextPath() %>/catalog" method="post" enctype="multipart/form-data">
                <h2>Добавить автомобиль</h2>
                <div class="form-group">
                    <label for="name">Название</label>
                    <input type="text" class="form-control" id="name" name="name" placeholder="Auto name" required>
                </div>
                <div class="form-group">
                    <label for="description">Описание</label>
                    <textarea name="description" class="form-control" id="description" placeholder="Auto description" required></textarea>
                </div>
                <div class="custom-file">
                    <input type="file" class="custom-file-input" id="file" name="file" accept="image/*" required>
                    <label class="custom-file-label" for="file">Выбрать файл</label>
                </div>

                <button type="submit" class="btn btn-primary mt-2">Создать</button>
            </form>
        </div>
    </div>
</div>


</body>
</html>