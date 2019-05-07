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

            <form class="mt-3" action="<%= request.getContextPath() %>/catalog" method="get" name="searchForm">

                <div class="btn-group" role="group">
                    <%-- выпадающий список --%>
                    <select class="custom-select" name="inputSelect">
                        <option selected value="name">Название</option>
                        <option value="description">Описание</option>
                        <option value="year">Год выпуска</option>
                        <option value="power">Мощность</option>
                        <option value="color">Цвет</option>
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
                        <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top img-thumbnail">
                        <div class="card-body">
                            <small>Название:</small> <h5 class="card-title"><%= item.getName() %></h5>
                            <small>Описание:</small> <p class="card-text"><%= item.getDescription()%></p>
                            <small>Год выпуска:</small> <p class="card-text"><%= item.getYear()%></p>
                            <small>Мощность:</small> <p class="card-text"><%= item.getPower()%></p>
                            <small>Цвет:</small> <p class="card-text"><%= item.getColor()%></p>
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
                    <input type="text" class="form-control" id="name" name="name" placeholder="Название" required>
                </div>
                <div class="form-group">
                    <label for="description">Описание</label>
                    <input type="text" name="description" class="form-control" id="description" placeholder="Описание" required>
                </div>
                <div class="form-group">
                    <label for="year">Год выпуска</label>
                    <input type="number" step="1" min="2018" name="year" class="form-control" id="year" placeholder="Год выпуска" required>
                </div>
                <div class="form-group">
                    <label for="year">Мощность</label>
                    <input type="number" step="0.1" min="0.5" name="power" class="form-control" id="power" placeholder="Мощность" required>
                </div>
                <div class="form-group">
                    <label for="year">Цвет</label>
                    <input type="text" name="color" class="form-control" id="color" placeholder="Цвет" required>
                </div>
                <div class="form-group">
                    <div class="btn btn-light mt-2">
                        <input type="file" id="file" name="file" accept="image/*" required>
                    </div>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary mt-2">Создать</button>
                </div>
            </form>

            <form class="mt-3" action="<%= request.getContextPath() %>/catalog" method="post" enctype="multipart/form-data">
                <h2>Добавить автомобиль из csv-файла</h2>
                <div class="form-group">
                    <div class="btn btn-light mt-2">
                        <input type="file" id="csvFile" name="csvFile" accept="text/csv" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary mt-2">Создать</button>
            </form>
        </div>
    </div>
</div>


</body>
</html>