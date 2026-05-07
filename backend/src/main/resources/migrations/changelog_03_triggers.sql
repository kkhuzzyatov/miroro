create or replace function increment_variant_quantity()
returns trigger as $$
begin
    update variant
    set quantity = quantity + 1
    where variant_id = new.variant_id;

    return new;
end;
$$ language plpgsql;

drop trigger if exists trg_increment_variant_quantity on product_item;

create trigger trg_increment_variant_quantity
after insert on product_item
for each row
execute function increment_variant_quantity();

create or replace function decrement_variant_quantity()
returns trigger as $$
begin
    update variant v
    set quantity = quantity - 1
    from product_item pi
    where pi.product_item_id = new.product_item_id
      and v.variant_id = pi.variant_id;

    return new;
end;
$$ language plpgsql;

drop trigger if exists trg_decrement_variant_quantity on purchase_item;

create trigger trg_decrement_variant_quantity
after insert on purchase_item
for each row
execute function decrement_variant_quantity();
