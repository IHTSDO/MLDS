--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: blob_write(bytea); Type: FUNCTION; Schema: public; Owner: mlds
--

CREATE FUNCTION blob_write(lbytea bytea) RETURNS oid
    LANGUAGE plpgsql
    AS $$
   declare
      loid oid;
      lfd integer;
      lsize integer;
begin
   if(lbytea is null) then
      return null;
   end if;
 
   loid := lo_create(0);
   lfd := lo_open(loid,131072);
   lsize := lowrite(lfd,lbytea);
   perform lo_close(lfd);
   return loid;
end;
$$;


ALTER FUNCTION public.blob_write(lbytea bytea) OWNER TO mlds;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: affiliate; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE affiliate (
    affiliate_id bigint NOT NULL,
    created timestamp with time zone,
    creator character varying(255),
    application_id bigint,
    type character varying(255),
    affiliate_details_id bigint,
    home_member_id bigint NOT NULL,
    import_key character varying(255),
    notes_internal text,
    standing_state character varying(255) NOT NULL
);


ALTER TABLE public.affiliate OWNER TO mlds;

--
-- Name: affiliate_details; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE affiliate_details (
    affiliate_details_id bigint NOT NULL,
    first_name character varying(50),
    last_name character varying(50),
    email character varying(100),
    alternate_email character varying(100),
    third_email character varying(100),
    street character varying(255),
    city character varying(255),
    post character varying(50),
    billing_street character varying(255),
    billing_city character varying(255),
    billing_post character varying(50),
    country_iso_code_2 character varying(255),
    billing_country_iso_code_2 character varying(255),
    organization_name character varying(255),
    landline_number character varying(255),
    landline_extension character varying(255),
    mobile_number character varying(255),
    organization_type character varying(255),
    organization_type_other character varying(255),
    subtype character varying(255),
    type character varying(255),
    other_text character varying(255),
    agreement_type character varying(255)
);


ALTER TABLE public.affiliate_details OWNER TO mlds;

--
-- Name: application; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE application (
    application_id numeric NOT NULL,
    username character varying(255),
    street character varying(255),
    city character varying(255),
    country character varying(255),
    landline_extension character varying(255),
    full_name character varying(255),
    landline_number character varying(255),
    snomedlicense boolean DEFAULT false NOT NULL,
    email character varying(255),
    alternate_email character varying(255),
    third_email character varying(255),
    mobile_number character varying(255),
    organization_name character varying(255),
    organization_type character varying(255),
    billing_street character varying(255),
    billing_city character varying(255),
    billing_country character varying(255),
    billing_post_code character varying(255),
    post_code character varying(255),
    organization_type_other character varying(255),
    submitted_at timestamp with time zone,
    notes_internal text,
    completed_at timestamp with time zone,
    approval_state character varying(255) DEFAULT 'NOT_SUBMITTED'::character varying NOT NULL,
    commercial_usage_id bigint,
    affiliate_details_id bigint,
    member_id bigint NOT NULL,
    application_type character varying(255) NOT NULL,
    reason text,
    affiliate_id bigint,
    created_at timestamp with time zone
);


ALTER TABLE public.application OWNER TO mlds;

--
-- Name: commercial_usage; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE commercial_usage (
    commercial_usage_id bigint NOT NULL,
    created timestamp with time zone,
    key character varying(255),
    start_date date,
    end_date date,
    note text,
    approval_state character varying(255) DEFAULT 'NOT_SUBMITTED'::character varying,
    submitted timestamp with time zone,
    affiliate_id bigint,
    current_usage text,
    planned_usage text,
    purpose text,
    type character varying(255),
    implementation_status character varying(255),
    effective_to timestamp with time zone,
    other_activities text
);


ALTER TABLE public.commercial_usage OWNER TO mlds;

--
-- Name: commercial_usage_count; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE commercial_usage_count (
    commercial_usage_count_id bigint NOT NULL,
    commercial_usage_id bigint,
    created timestamp with time zone,
    country_iso_code_2 character varying(255),
    notes text,
    snomed_practices integer,
    hospitals_staffing_practices integer,
    data_creation_practices_not_part_of_hospital integer,
    non_practice_data_creation_systems integer,
    deployed_data_analysis_systems integer,
    databases_per_deployment integer
);


ALTER TABLE public.commercial_usage_count OWNER TO mlds;

--
-- Name: commercial_usage_entry; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE commercial_usage_entry (
    commercial_usage_entry_id bigint NOT NULL,
    created timestamp with time zone,
    name character varying(255),
    start_date date,
    end_date date,
    country_iso_code_2 character varying(255),
    commercial_usage_id bigint,
    note text
);


ALTER TABLE public.commercial_usage_entry OWNER TO mlds;

--
-- Name: country; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE country (
    iso_code_2 character varying(2) NOT NULL,
    iso_code_3 character varying(3),
    common_name character varying(255),
    exclude_registration boolean DEFAULT false NOT NULL,
    alternate_registration_url character varying(255),
    member_id bigint
);


ALTER TABLE public.country OWNER TO mlds;

--
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp with time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20)
);


ALTER TABLE public.databasechangelog OWNER TO mlds;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp with time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO mlds;

--
-- Name: email_domain_blacklist; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE email_domain_blacklist (
    domain_id numeric NOT NULL,
    domainname character varying(255)
);


ALTER TABLE public.email_domain_blacklist OWNER TO mlds;

--
-- Name: event; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE event (
    event_id bigint NOT NULL,
    type character varying(31) NOT NULL,
    description character varying(4096) NOT NULL,
    "timestamp" timestamp with time zone NOT NULL,
    event_sub_type character varying(255),
    principal character varying(255),
    browser_type character varying(32),
    browser_version character varying(32),
    ip_address character varying(32),
    locale character varying(5),
    session_id character varying(255),
    user_agent character varying(1024)
);


ALTER TABLE public.event OWNER TO mlds;

--
-- Name: file; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE file (
    file_id bigint NOT NULL,
    filename character varying(255),
    updated_at timestamp with time zone,
    creator character varying(100),
    mimetype character varying(150),
    content oid
);


ALTER TABLE public.file OWNER TO mlds;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: mlds
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO mlds;

--
-- Name: member; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE member (
    member_id bigint NOT NULL,
    key character varying(255) NOT NULL,
    created_at timestamp with time zone,
    license_file bigint,
    license_name character varying(255),
    license_version character varying(255),
    name character varying(255),
    logo_file bigint
);


ALTER TABLE public.member OWNER TO mlds;

--
-- Name: password_reset_token; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE password_reset_token (
    password_reset_token_id character varying(255) NOT NULL,
    created timestamp with time zone,
    user_id bigint NOT NULL
);


ALTER TABLE public.password_reset_token OWNER TO mlds;

--
-- Name: release_file; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE release_file (
    release_file_id bigint NOT NULL,
    release_version_id bigint,
    created_at timestamp with time zone,
    label character varying(255),
    download_url text
);


ALTER TABLE public.release_file OWNER TO mlds;

--
-- Name: release_package; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE release_package (
    release_package_id bigint NOT NULL,
    created_by character varying(255),
    created_at timestamp with time zone,
    name character varying(255),
    description text,
    inactive_at timestamp with time zone,
    member_id bigint NOT NULL
);


ALTER TABLE public.release_package OWNER TO mlds;

--
-- Name: release_version; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE release_version (
    release_version_id bigint NOT NULL,
    release_package_id bigint,
    created_by character varying(255),
    created_at timestamp with time zone,
    name character varying(255),
    description text,
    published_at date,
    online boolean DEFAULT false NOT NULL,
    inactive_at timestamp with time zone
);


ALTER TABLE public.release_version OWNER TO mlds;

--
-- Name: t_authority; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_authority (
    name character varying(255) NOT NULL
);


ALTER TABLE public.t_authority OWNER TO mlds;

--
-- Name: t_persistent_audit_event; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_persistent_audit_event (
    event_id bigint NOT NULL,
    principal character varying(50),
    event_date timestamp with time zone,
    event_type character varying(50),
    application_id bigint,
    affiliate_id bigint,
    release_package_id bigint,
    release_version_id bigint,
    release_file_id bigint,
    commercial_usage_id bigint
);


ALTER TABLE public.t_persistent_audit_event OWNER TO mlds;

--
-- Name: t_persistent_audit_event_data; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_persistent_audit_event_data (
    event_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(255)
);


ALTER TABLE public.t_persistent_audit_event_data OWNER TO mlds;

--
-- Name: t_persistent_token; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_persistent_token (
    series character varying(255) NOT NULL,
    token_value character varying(255),
    token_date date,
    ip_address character varying(39),
    user_agent character varying(255),
    user_id bigint
);


ALTER TABLE public.t_persistent_token OWNER TO mlds;

--
-- Name: t_user; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_user (
    login character varying(50) NOT NULL,
    password character varying(100),
    first_name character varying(50),
    last_name character varying(50),
    email character varying(100),
    activated boolean DEFAULT true NOT NULL,
    lang_key character varying(5),
    activation_key character varying(20),
    created_by character varying(50) DEFAULT 'system'::character varying NOT NULL,
    created_date timestamp with time zone DEFAULT now() NOT NULL,
    last_modified_by character varying(50),
    last_modified_date timestamp with time zone,
    user_id bigint NOT NULL
);


ALTER TABLE public.t_user OWNER TO mlds;

--
-- Name: t_user_authority; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_user_authority (
    name character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.t_user_authority OWNER TO mlds;

--
-- Name: user_registration; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE user_registration (
    user_registration_id numeric NOT NULL,
    email character varying(255)
);


ALTER TABLE public.user_registration OWNER TO mlds;

--
-- Name: EventPK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY event
    ADD CONSTRAINT "EventPK" PRIMARY KEY (event_id);


--
-- Name: affiliate_details_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY affiliate_details
    ADD CONSTRAINT "affiliate_details_PK" PRIMARY KEY (affiliate_details_id);


--
-- Name: affiliate_import_key_home_member_id_key; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY affiliate
    ADD CONSTRAINT affiliate_import_key_home_member_id_key UNIQUE (import_key, home_member_id);


--
-- Name: applicationPK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY application
    ADD CONSTRAINT "applicationPK" PRIMARY KEY (application_id);


--
-- Name: commercial_usage_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY commercial_usage
    ADD CONSTRAINT "commercial_usage_PK" PRIMARY KEY (commercial_usage_id);


--
-- Name: commercial_usage_count_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY commercial_usage_count
    ADD CONSTRAINT "commercial_usage_count_PK" PRIMARY KEY (commercial_usage_count_id);


--
-- Name: commercial_usage_entry_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY commercial_usage_entry
    ADD CONSTRAINT "commercial_usage_entry_PK" PRIMARY KEY (commercial_usage_entry_id);


--
-- Name: email_domain_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY email_domain_blacklist
    ADD CONSTRAINT "email_domain_PK" PRIMARY KEY (domain_id);


--
-- Name: licensee_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY affiliate
    ADD CONSTRAINT "licensee_PK" PRIMARY KEY (affiliate_id);


--
-- Name: member_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY member
    ADD CONSTRAINT "member_PK" PRIMARY KEY (member_id);


--
-- Name: member_name_unique; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_name_unique UNIQUE (key);


--
-- Name: package_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY release_package
    ADD CONSTRAINT "package_PK" PRIMARY KEY (release_package_id);


--
-- Name: password_reset_tokenPK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY password_reset_token
    ADD CONSTRAINT "password_reset_tokenPK" PRIMARY KEY (password_reset_token_id);


--
-- Name: pk_country; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY country
    ADD CONSTRAINT pk_country PRIMARY KEY (iso_code_2);


--
-- Name: pk_databasechangeloglock; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


--
-- Name: pk_file; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT pk_file PRIMARY KEY (file_id);


--
-- Name: pk_t_authority; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_authority
    ADD CONSTRAINT pk_t_authority PRIMARY KEY (name);


--
-- Name: pk_t_persistent_audit_event; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_persistent_audit_event
    ADD CONSTRAINT pk_t_persistent_audit_event PRIMARY KEY (event_id);


--
-- Name: pk_t_persistent_token; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_persistent_token
    ADD CONSTRAINT pk_t_persistent_token PRIMARY KEY (series);


--
-- Name: release_file_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY release_file
    ADD CONSTRAINT "release_file_PK" PRIMARY KEY (release_file_id);


--
-- Name: release_version_PK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY release_version
    ADD CONSTRAINT "release_version_PK" PRIMARY KEY (release_version_id);


--
-- Name: t_persistent_audit_event_data_pkey; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_persistent_audit_event_data
    ADD CONSTRAINT t_persistent_audit_event_data_pkey PRIMARY KEY (event_id, name);


--
-- Name: t_user_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_user_authority
    ADD CONSTRAINT t_user_authority_pkey PRIMARY KEY (user_id, name);


--
-- Name: t_user_pkey; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_user
    ADD CONSTRAINT t_user_pkey PRIMARY KEY (user_id);


--
-- Name: user_registraPK; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY user_registration
    ADD CONSTRAINT "user_registraPK" PRIMARY KEY (user_registration_id);


--
-- Name: idx_persistent_audit_event; Type: INDEX; Schema: public; Owner: mlds; Tablespace: 
--

CREATE INDEX idx_persistent_audit_event ON t_persistent_audit_event USING btree (principal, event_date);


--
-- Name: idx_persistent_audit_event_data; Type: INDEX; Schema: public; Owner: mlds; Tablespace: 
--

CREATE INDEX idx_persistent_audit_event_data ON t_persistent_audit_event_data USING btree (event_id);


--
-- Name: FK_affiliate_affiliate_details; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY affiliate
    ADD CONSTRAINT "FK_affiliate_affiliate_details" FOREIGN KEY (affiliate_details_id) REFERENCES affiliate_details(affiliate_details_id);


--
-- Name: FK_affiliate_details_billing_country; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY affiliate_details
    ADD CONSTRAINT "FK_affiliate_details_billing_country" FOREIGN KEY (billing_country_iso_code_2) REFERENCES country(iso_code_2);


--
-- Name: FK_affiliate_details_country; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY affiliate_details
    ADD CONSTRAINT "FK_affiliate_details_country" FOREIGN KEY (country_iso_code_2) REFERENCES country(iso_code_2);


--
-- Name: FK_application_affiliate; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY application
    ADD CONSTRAINT "FK_application_affiliate" FOREIGN KEY (affiliate_id) REFERENCES affiliate(affiliate_id);


--
-- Name: FK_application_affiliate_details; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY application
    ADD CONSTRAINT "FK_application_affiliate_details" FOREIGN KEY (affiliate_details_id) REFERENCES affiliate_details(affiliate_details_id);


--
-- Name: FK_application_commercial_usage; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY application
    ADD CONSTRAINT "FK_application_commercial_usage" FOREIGN KEY (commercial_usage_id) REFERENCES commercial_usage(commercial_usage_id);


--
-- Name: FK_commercial_usage_count_commercial_usage; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY commercial_usage_count
    ADD CONSTRAINT "FK_commercial_usage_count_commercial_usage" FOREIGN KEY (commercial_usage_id) REFERENCES commercial_usage(commercial_usage_id);


--
-- Name: FK_commercial_usage_count_country; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY commercial_usage_count
    ADD CONSTRAINT "FK_commercial_usage_count_country" FOREIGN KEY (country_iso_code_2) REFERENCES country(iso_code_2);


--
-- Name: FK_commercial_usage_entry_commercial_usage; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY commercial_usage_entry
    ADD CONSTRAINT "FK_commercial_usage_entry_commercial_usage" FOREIGN KEY (commercial_usage_id) REFERENCES commercial_usage(commercial_usage_id);


--
-- Name: FK_commercial_usage_entry_country; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY commercial_usage_entry
    ADD CONSTRAINT "FK_commercial_usage_entry_country" FOREIGN KEY (country_iso_code_2) REFERENCES country(iso_code_2);


--
-- Name: FK_commercial_usage_licensee; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY commercial_usage
    ADD CONSTRAINT "FK_commercial_usage_licensee" FOREIGN KEY (affiliate_id) REFERENCES affiliate(affiliate_id);


--
-- Name: FK_event_persistent_audit_event_data; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_persistent_audit_event_data
    ADD CONSTRAINT "FK_event_persistent_audit_event_data" FOREIGN KEY (event_id) REFERENCES t_persistent_audit_event(event_id);


--
-- Name: FK_licensee_application; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY affiliate
    ADD CONSTRAINT "FK_licensee_application" FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: FK_release_file_release_version; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY release_file
    ADD CONSTRAINT "FK_release_file_release_version" FOREIGN KEY (release_version_id) REFERENCES release_version(release_version_id);


--
-- Name: FK_release_version_release_package; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY release_version
    ADD CONSTRAINT "FK_release_version_release_package" FOREIGN KEY (release_package_id) REFERENCES release_package(release_package_id);


--
-- Name: FK_t_persistent_audit_event_commercial_usage; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_persistent_audit_event
    ADD CONSTRAINT "FK_t_persistent_audit_event_commercial_usage" FOREIGN KEY (commercial_usage_id) REFERENCES commercial_usage(commercial_usage_id);


--
-- Name: affiliate_home_member; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY affiliate
    ADD CONSTRAINT affiliate_home_member FOREIGN KEY (home_member_id) REFERENCES member(member_id);


--
-- Name: application_member; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY application
    ADD CONSTRAINT application_member FOREIGN KEY (member_id) REFERENCES member(member_id);


--
-- Name: country_member; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_member FOREIGN KEY (member_id) REFERENCES member(member_id);


--
-- Name: fk_authority_name; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_user_authority
    ADD CONSTRAINT fk_authority_name FOREIGN KEY (name) REFERENCES t_authority(name);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_user_authority
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES t_user(user_id);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_persistent_token
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES t_user(user_id);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY password_reset_token
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES t_user(user_id);


--
-- Name: logo_file; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY member
    ADD CONSTRAINT logo_file FOREIGN KEY (logo_file) REFERENCES file(file_id);


--
-- Name: member_file; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_file FOREIGN KEY (license_file) REFERENCES file(file_id);


--
-- Name: release_package_member; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY release_package
    ADD CONSTRAINT release_package_member FOREIGN KEY (member_id) REFERENCES member(member_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

