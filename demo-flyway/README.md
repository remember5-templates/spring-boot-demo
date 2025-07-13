# IDEA Blog

https://blog.jetbrains.com/idea/2024/11/how-to-use-flyway-for-database-migrations-in-spring-boot-applications/


```markdown

Flyway is an open-source database migration tool that simplifies the process of managing and versioning database schema changes. 

With Flyway, migration scripts are stored alongside application code, following a consistent, versioned approach that allows teams to manage database changes as part of their regular development workflow. Flyway supports a wide range of databases, including MySQL, PostgreSQL, Oracle, SQL Server, and many others.

Flyway uses a simple versioning system to manage migration scripts. Each script is assigned a unique version number (e.g. V1__init.sql, V2__create_articles_table.sql), which Flyway uses to track which scripts have been applied and which are pending.

The naming convention for versioned migrations is:{Prefix}{Version}{Separator}{Description}{Suffix}

By default, for versioned migrations, {Prefix} is V, {Separator} is __, and {Suffix} is .sql.

Some example names for Flyway database migrations are:

V1__Init_Setup.sql
V2__Add_status_col.sql
V3.1__create_url_index.sql
V3.2__add_updated_by_column_to_bookmarks_table.sql
V4__Add_tags_table.sql
Once the migration scripts are created, you can apply them to a database either using the Flyway Java API or using the Flyway Maven or Gradle plugin.

Once applied, Flyway keeps track of the applied migrations in a table called flyway_schema_history as shown below:


```
