-- Create User
CREATE OR REPLACE function new_user(
    name varchar,
    username varchar,
    email varchar,
    password varchar,
    phone varchar
    )
  RETURNS int AS
$BODY$
DECLARE
   u_id int;
BEGIN
   Insert into users (name,username,email,password,phone)
   VALUES (name,username,email,password,phone)
   RETURNING id INTO u_id;

    RETURN u_id;
END;
$BODY$
  LANGUAGE plpgsql;

-- Edit User
create or replace procedure edit_user(
   userID int,d
   params TEXT [][2]
)
language plpgsql
as $$
begin
    FOR i IN array_lower(params, 1)..array_upper(params, 1) LOOP
        EXECUTE format('UPDATE users SET %I = %L WHERE id = $1;', params[i][1], params[i][2])
                USING (userID);
    END LOOP;
end;$$


-- Get User

--
-- TOC entry 218 (class 1255 OID 84843)
-- Name: delete_user(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_user(user_id int) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
DELETE FROM "users" u WHERE u.id=user_id;
END; $$;


ALTER FUNCTION public.delete_user(user_id int) OWNER TO postgres;

--
-- TOC entry 219 (class 1255 OID 84844)
-- Name: edit_user(integer, text[]); Type: PROCEDURE; Schema: public; Owner: postgres
--


--
-- TOC entry 217 (class 1255 OID 84842)
-- Name: get_user(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_user(user_name varchar ) RETURNS TABLE(username varchar , password varchar , id int)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT
         u.username as uname,
		 u.password as pass,
		 u.id as uid
    FROM
        "users" u
    WHERE
        u.username = user_name;
END; $$;


ALTER FUNCTION public.get_user(user_name varchar ) OWNER TO postgres;

--
-- TOC entry 204 (class 1255 OID 76593)
-- Name: new_user(character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--
