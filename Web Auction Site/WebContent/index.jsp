<html>
  <head>
    <link rel="stylesheet" type="text/css" href="res/styles.css">
    <link rel="icon" href="res/favicon.ico" type="image/x-icon" />
    <title>Web Auction Site by RobCo Industries</title>
  </head>
  <body>
    <header class="menubar">
      <a onClick=location.assign("login.jsp");>Login</a> |
      <a onClick=location.assign("register.jsp");>Register</a>
    </header>
    <div id="search">
      <h1>Web Auction Site</h1>
      <form>
        <div class="txtbtncontainer">
          <input type="text" placeholder="Search..." class="textbox-search" name="search_terms" />
          <input type="submit" class="button-search" value="Search"/>
          <select name="category" class="select-search">
            <option value="all">All Categories</option>
            <option value="tech">Technology</option>
            <option value="clothes">Clothing</option>
          </select>
        </div>
      </form>
    </div>
    <footer>RobCo Industries 2016</footer>
  </body>
</html>
  
