-- add MPA ratings
INSERT INTO mpa (name)
SELECT *
FROM (
    VALUES ('G'),
        ('PG'),
        ('PG-13'),
        ('R'),
        ('NC-17')
    ) data
WHERE NOT EXISTS (
    SELECT NULL
    FROM mpa
);

-- add genres
INSERT INTO genres (name)
SELECT *
FROM (
    VALUES ('Комедия'),
        ('Драма'),
        ('Мультфильм'),
        ('Триллер'),
        ('Документальный'),
        ('Боевик')
    ) data
WHERE NOT EXISTS (
    SELECT NULL
    FROM genres
);
