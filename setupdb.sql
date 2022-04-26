create table images (
    id uuid primary key,
    label text,
    url text
);


create table image_metadata (
    id uuid primary key,
    image_id uuid references images(id),
    description text,
    confidence_score double precision
);