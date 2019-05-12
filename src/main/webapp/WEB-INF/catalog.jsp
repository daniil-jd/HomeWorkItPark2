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
    <div class="row no-gutters">
        <div class="col">
            <div>
                <h1>Каталог</h1>
            </div>

            <form class="mt-2" action="<%= request.getContextPath() %>/catalog" method="get" name="searchForm">

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
                    <div class="ml-2">
                        <input name="query" class="form-control" type="search" placeholder="Поиск">
                    </div>
                </div>
            </form>

            <% if (((List<Auto>)request.getAttribute("items")).size() > 0) { %>
            <div class="row no-gutters">
                <% for (Auto item : (List<Auto>)request.getAttribute("items")) { %>
                <div class="col-sm-4 mt-2 pr-2">
                    <div class="card">
                        <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top img-thumbnail">
                        <div class="card-body ml-1">
                            <small>Название:</small> <h5 class="card-title"><%= item.getName() %></h5>
                            <small>Описание:</small> <p class="card-text"><%= item.getDescription()%></p>
                            <small>Год выпуска:</small> <p class="card-text"><%= item.getYear()%></p>
                            <small>Мощность:</small> <p class="card-text"><%= item.getPower()%></p>
                            <small>Цвет:</small> <p class="card-text"><%= item.getColor()%></p>
                            <div class="text-center mb-1">
                                <a href="<%= request.getContextPath() %>/details/<%= item.getId() %>" class="btn btn-primary">Детали</a>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>

            <form action="<%= request.getContextPath() %>/catalog" method="post" enctype="multipart/form-data">
                <button class="btn btn-secondary my-2" type="submit" id="load" name="load">Сохранить автомобили</button>
            </form>

            <% } %>

            <form class="card mt-2 p-1" action="<%= request.getContextPath() %>/catalog" method="post" enctype="multipart/form-data">
                <div>
                    <h2>Добавить автомобиль</h2>
                </div>
                <div class="form-group mb-2">
                    <label class="mb-0" for="name">Название</label>
                    <input type="text" class="form-control" id="name" name="name" placeholder="Название" required>
                </div>
                <div class="form-group mb-2">
                    <label class="mb-0" for="description">Описание</label>
                    <input type="text" name="description" class="form-control" id="description" placeholder="Описание" required>
                </div>
                <div class="form-group mb-2">
                    <label class="mb-0" for="year">Год выпуска</label>
                    <input type="number" step="1" min="2018" name="year" class="form-control" id="year" placeholder="Год выпуска" required>
                </div>
                <div class="form-group mb-2">
                    <label class="mb-0" for="year">Мощность</label>
                    <input type="number" step="0.1" min="0.5" name="power" class="form-control" id="power" placeholder="Мощность" required>
                </div>
                <div class="form-group mb-2">
                    <label class="mb-0" for="year">Цвет</label>
                    <input type="text" name="color" class="form-control" id="color" placeholder="Цвет" required>
                </div>
                <div class="form-group mb-2">
                    <input type="file" id="file" name="file" accept="image/*" class="form-control-file" required>
                </div>
                <div class="form-group mb-0">
                    <button type="submit" class="btn btn-primary">Создать</button>
                </div>
            </form>

            <form class="card mt-2 p-1" action="<%= request.getContextPath() %>/catalog" method="post" enctype="multipart/form-data">
                <div>
                    <h2>Загрузить автомобили из csv-файла</h2>
                </div>
                <div class="form-group mb-2">
                    <input type="file" id="csvFile" name="csvFile" accept="text/csv" class="form-control-file" required>
                </div>
                <div class="form-group mb-0">
                    <button type="submit" class="btn btn-primary">Создать</button>
                </div>
            </form>
        </div>
    </div>
</div>


</body>
</html>