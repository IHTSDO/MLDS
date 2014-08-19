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
    import_key character varying(255)
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
    organization_type_other character varying(255)
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
    type character varying(255),
    subtype character varying(255),
    email character varying(255),
    alternate_email character varying(255),
    third_email character varying(255),
    mobile_number character varying(255),
    organization_name character varying(255),
    organization_type character varying(255),
    billing_street character varying(255),
    billing_city character varying(255),
    billing_country character varying(255),
    other_text character varying(255),
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
    affiliate_id bigint
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
    agreement_type character varying(255),
    type character varying(255),
    implementation_status character varying(255)
);


ALTER TABLE public.commercial_usage OWNER TO mlds;

--
-- Name: commercial_usage_count; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE commercial_usage_count (
    commercial_usage_count_id bigint NOT NULL,
    commercial_usage_id bigint,
    created timestamp with time zone,
    practices integer DEFAULT 0,
    country_iso_code_2 character varying(255)
);


ALTER TABLE public.commercial_usage_count OWNER TO mlds;

--
-- Name: commercial_usage_entry; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE commercial_usage_entry (
    commercial_usage_entry_id bigint NOT NULL,
    created timestamp with time zone,
    name character varying(255),
    start_date date NOT NULL,
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
    created_at timestamp with time zone
);


ALTER TABLE public.member OWNER TO mlds;

--
-- Name: password_reset_token; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE password_reset_token (
    password_reset_token_id character varying(255) NOT NULL,
    user_login character varying(255),
    created timestamp with time zone
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
    published_at timestamp with time zone,
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
    release_file_id bigint
);


ALTER TABLE public.t_persistent_audit_event OWNER TO mlds;

--
-- Name: t_persistent_audit_event_data; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_persistent_audit_event_data (
    event_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    value character varying(255)
);


ALTER TABLE public.t_persistent_audit_event_data OWNER TO mlds;

--
-- Name: t_persistent_token; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_persistent_token (
    series character varying(255) NOT NULL,
    user_login character varying(50),
    token_value character varying(255),
    token_date date,
    ip_address character varying(39),
    user_agent character varying(255)
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
    last_modified_date timestamp with time zone
);


ALTER TABLE public.t_user OWNER TO mlds;

--
-- Name: t_user_authority; Type: TABLE; Schema: public; Owner: mlds; Tablespace: 
--

CREATE TABLE t_user_authority (
    login character varying(50) NOT NULL,
    name character varying(255) NOT NULL
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
-- Data for Name: affiliate; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY affiliate (affiliate_id, created, creator, application_id, type, affiliate_details_id, home_member_id, import_key) FROM stdin;
2053	2014-08-05 14:43:46.587-04	me@bad1.com	2055	COMMERCIAL	\N	1437	\N
2120	2014-08-05 16:18:05.815-04	able945@mailinator.com	2122	COMMERCIAL	\N	1438	\N
2130	2014-08-05 17:11:50.247-04	able946@mailinator.com	2132	COMMERCIAL	2146	1438	\N
2169	2014-08-06 10:01:10.829-04	able947@mailinator.com	2171	ACADEMIC	2207	1438	\N
2654	2014-08-12 15:19:04.822-04	able951@mailinator.com	2656	INDIVIDUAL	2669	1438	\N
2218	2014-08-06 10:36:52.523-04	able948@mailinator.com	2220	INDIVIDUAL	2249	1438	\N
111	2014-06-26 15:34:50.634-04	admin	\N	COMMERCIAL	\N	1437	\N
119	2014-06-26 15:39:46.22-04	able918@mailinator.com	\N	COMMERCIAL	\N	1437	\N
139	2014-06-26 16:23:42.319-04	able919@mailinator.com	137	COMMERCIAL	\N	1437	\N
704	2014-07-07 13:42:10.134-04	able921@mailinator.com	703	COMMERCIAL	\N	1437	\N
733	2014-07-07 14:25:05.977-04	able922@mailinator.com	732	COMMERCIAL	\N	1437	\N
306	2014-07-03 11:48:44.884-04	able920@mailinator.com	305	COMMERCIAL	\N	1437	\N
1019	2014-07-09 12:25:57.078-04	able923@mailinator.com	1018	COMMERCIAL	\N	1437	\N
1164	2014-07-18 13:13:25.494-04	able925@mailinator.com	1163	COMMERCIAL	\N	1437	\N
1201	2014-07-18 15:40:44.168-04	able926@mailinator.com	1200	COMMERCIAL	\N	1437	\N
1209	2014-07-18 15:51:36.255-04	able927@mailinator.com	1208	COMMERCIAL	\N	1437	\N
1255	2014-07-21 11:45:46.9-04	able928@mailinator.com	1254	COMMERCIAL	\N	1437	\N
1264	2014-07-21 11:53:24.342-04	able929@mailinator.com	1263	COMMERCIAL	\N	1437	\N
1270	2014-07-21 12:10:21.18-04	able930@mailinator.com	1269	COMMERCIAL	\N	1437	\N
1278	2014-07-21 12:24:24.074-04	able931@mailinator.com	1277	COMMERCIAL	\N	1437	\N
1305	2014-07-21 16:22:38.001-04	able931@intelliware.ca	1304	COMMERCIAL	\N	1437	\N
1338	2014-07-21 17:02:11.676-04	able932@mailinator.com	1337	COMMERCIAL	\N	1437	\N
1384	2014-07-23 12:28:34.344-04	able933@mailinator.com	1383	COMMERCIAL	\N	1437	\N
1426	2014-07-23 16:30:44.461-04	able934@mailinator.com	1425	COMMERCIAL	\N	1437	\N
1441	2014-07-25 10:39:07.048-04	able935@mailinator.com	1440	COMMERCIAL	\N	1437	\N
1514	2014-07-28 16:21:45.746-04	able937@mailinator.com	1513	COMMERCIAL	1522	1437	\N
1571	2014-07-29 15:48:40.718-04	able938@mailinator.com	1570	COMMERCIAL	\N	1438	\N
1495	2014-07-28 11:26:29.962-04	able936@mailinator.com	1494	COMMERCIAL	1704	1437	\N
2296	2014-08-06 13:19:32.028-04	able949@mailinator.com	2298	COMMERCIAL	2358	1438	\N
1733	2014-07-31 12:03:47.357-04	able939@mailinator.com	1732	COMMERCIAL	1810	1437	\N
2367	2014-08-08 10:22:38.586-04	able950@mailinator.com	2369	INDIVIDUAL	\N	1437	\N
1820	2014-07-31 15:16:42.433-04	able940@mailinator.com	1819	INDIVIDUAL	1829	1438	\N
1840	2014-07-31 15:32:51.346-04	able941@mailinator.com	1839	ACADEMIC	1852	1438	\N
1860	2014-07-31 15:35:39.92-04	able942@mailinator.com	1859	COMMERCIAL	1869	1438	\N
2008	2014-08-05 13:57:02.251-04	able943@mailinator.com	2010	COMMERCIAL	\N	1437	\N
2033	2014-08-05 14:32:03.159-04	able944@mailinator.com	2035	COMMERCIAL	\N	1437	\N
2758	2014-08-13 16:24:32.152-04	\N	2754	COMMERCIAL	2757	1438	9
2695	2014-08-13 12:29:11.446-04	\N	2691	COMMERCIAL	2694	1438	91
2706	2014-08-13 12:30:05.017-04	\N	2702	COMMERCIAL	2705	1438	92
2427	2014-08-11 10:16:00.245-04	\N	2425	COMMERCIAL	2426	1438	93
2450	2014-08-11 12:45:20.098-04	\N	2448	\N	2449	1438	94
2458	2014-08-11 12:50:54.74-04	\N	2456	COMMERCIAL	2457	1438	95
2466	2014-08-11 13:43:40.964-04	\N	2464	COMMERCIAL	2465	1438	96
2501	2014-08-11 15:53:07.75-04	\N	2498	COMMERCIAL	2500	1438	97
2519	2014-08-11 16:06:40.493-04	\N	2515	COMMERCIAL	2518	1438	98
2531	2014-08-11 16:24:09.564-04	\N	2527	COMMERCIAL	2530	1438	99
2543	2014-08-11 16:30:11.376-04	\N	2539	COMMERCIAL	2542	1438	100
2555	2014-08-11 16:51:53.093-04	\N	2551	COMMERCIAL	2554	1438	101
2570	2014-08-12 11:13:55.233-04	\N	2566	COMMERCIAL	2569	1438	102
2575	2014-08-12 11:13:57.556-04	\N	2571	COMMERCIAL	2574	1438	103
2592	2014-08-12 11:14:16.675-04	\N	2588	COMMERCIAL	2591	1438	104
2718	2014-08-13 12:36:40.363-04	\N	2714	COMMERCIAL	2717	1438	105
2730	2014-08-13 12:38:46.016-04	\N	2726	COMMERCIAL	2729	1438	106
2746	2014-08-13 15:05:29.191-04	\N	2742	COMMERCIAL	2745	1438	107
2763	2014-08-13 16:24:32.164-04	\N	2759	ACADEMIC	2762	1438	201
2700	2014-08-13 12:29:11.46-04	\N	2696	ACADEMIC	2699	1438	202
2430	2014-08-11 10:16:00.251-04	\N	2428	ACADEMIC	2429	1438	203
2453	2014-08-11 12:45:25.686-04	\N	2451	\N	2452	1438	204
2461	2014-08-11 12:50:54.748-04	\N	2459	ACADEMIC	2460	1438	205
2469	2014-08-11 13:43:40.974-04	\N	2467	ACADEMIC	2468	1438	206
2505	2014-08-11 15:53:34.198-04	\N	2502	ACADEMIC	2504	1438	207
2524	2014-08-11 16:07:21.981-04	\N	2520	ACADEMIC	2523	1438	208
2536	2014-08-11 16:24:10.466-04	\N	2532	ACADEMIC	2535	1438	209
2548	2014-08-11 16:30:11.392-04	\N	2544	ACADEMIC	2547	1438	210
2560	2014-08-11 16:51:53.106-04	\N	2556	ACADEMIC	2559	1438	211
2580	2014-08-12 11:13:59.263-04	\N	2576	ACADEMIC	2579	1438	212
2597	2014-08-12 11:14:17.413-04	\N	2593	ACADEMIC	2596	1438	213
2711	2014-08-13 12:30:05.026-04	\N	2707	ACADEMIC	2710	1438	214
2723	2014-08-13 12:36:40.377-04	\N	2719	ACADEMIC	2722	1438	215
2735	2014-08-13 12:38:46.031-04	\N	2731	ACADEMIC	2734	1438	216
2751	2014-08-13 15:05:29.205-04	\N	2747	ACADEMIC	2750	1438	217
2586	2014-08-12 11:14:00.757-04	\N	2582	ACADEMIC	2585	1438	218
2787	2014-08-14 11:04:47.727-04	\N	2783	COMMERCIAL	2786	1438	1
2792	2014-08-14 11:04:47.753-04	\N	2788	ACADEMIC	2791	1438	2
\.


--
-- Data for Name: affiliate_details; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY affiliate_details (affiliate_details_id, first_name, last_name, email, alternate_email, third_email, street, city, post, billing_street, billing_city, billing_post, country_iso_code_2, billing_country_iso_code_2, organization_name, landline_number, landline_extension, mobile_number, organization_type, organization_type_other) FROM stdin;
2249	able222	948	able948@mailinator.com			address	city	post				SE	\N		+1 (555) 		+1 (666) 77	\N	
1838	able	941	able941@mailinator.com	alt@email.com		address	city	post				SE	\N	941 org acad	+1 (33		+1 (333) 	PUBLIC_HEALTH_ORGANIZATION	
1493	able	936	able936@mailinator.com	alt@email.com	third@email.com	some address	some city	some post	some address	some city	some post	CA	CA	936 org	+1 (33		+1 (434) 	PRIVATE_HEALTH_ORGANIZATION	
1852	able	941	able941@mailinator.com	alt@email.com		address 2	city	post				SE	\N	941 org acad	+1 (33		+1 (333) 	PUBLIC_HEALTH_ORGANIZATION	
2429						\N	\N	\N	\N	\N	\N	\N	\N					\N	
2449	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	\N	Other
2452												\N	\N					\N	
2131	able	946	able946@mailinator.com	alt@email.com	third@email.com	address	city	post	address b	city b	post b	SE	SE	946 Org	+1 (555) 		+1 (555) 5	PRIVATE_HEALTH_ORGANIZATION	
1569	able	938	able938@mailinator.com	alt@email.com	third@email.com	street	city	post	street b	city b	post b	SE	BW	938 Org	+1 (333) 		+1 (333) 	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2457	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
1704	AAA	ZZZ	able936@mailinator.com	alt@email.com	third@email.com	some address	some city	some post	some address	some city	some post	CA	CA	\N	+1 (33		+1 (434) 	PRIVATE_HEALTH_ORGANIZATION	
1512	able	937	able937@mailinator.com	alt@email.com	third@email.com	street	city	post	street b	city b	post b	CA	AC	937 Org	+1 (22		+1 (222) 	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2146	able2	946	able946@mailinator.com	alt@email.com	third@email.com	address	city	post	address b2	city b	post b	SE	SE	946 Org	+1 (555) 		+1 (555) 5	PRIVATE_HEALTH_ORGANIZATION	
1731	able	939a	able939@mailinator.com	alt@email.com	third@email.com	address	city	post	address	city	post	CM	GB	939 Org	+1 (555) 		+1 (555) 5	PRIVATE_HEALTH_ORGANIZATION	
2460												\N	\N					\N	
2465	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2468												\N	\N					\N	
2499	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2500	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
1522	able	937a	able937@mailinator.com	alt@email.com	third@email.com	street 212ab	city 212	hello	street b	city b	post b	CA	AC	937 Org	+1 (224) 		+1 (222) 	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2297	able	949	able949@mailinator.com	alt@email.com	third@email.com	address	city	post	address	city	post	SE	SE	949 Org	+1 (444) 		+1 (444) 4	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
1858	able	942	able942@mailinator.com	al@email.com	third@email.com	address	city	post	address b	city b	post b	SE	GB	942 org commercial	+1 (444) 		+1 (444) 	PRIVATE_HEALTH_ORGANIZATION	
1818	able	940	able940@mailinator.com			address	city	post				SE	\N		+1 (444) 		+1 (444) 4	\N	
1829	able	940	able940@mailinator.com			address 1	city 1	post 1				SE	\N		+1 (444) 		+1 (444) 4	\N	
1869	able	942	able942@mailinator.com	al@email.com	third@email.com	address	city	post	address b	city b	post b	SE	CA	942 org commercial	+1 (444) 		+1 (444) 	PRIVATE_HEALTH_ORGANIZATION	
1810	able	939a	able939@mailinator.com	alt@email.com	third@email.com	address 2	city	post	address b	city b	post b	CM	US	939 Org	+1 (555) 		+1 (555) 5	PRIVATE_HEALTH_ORGANIZATION	
2009	able	943	able943@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	CA	\N	\N	\N	\N	\N	\N	\N
2034	able	944	able944@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	AS	\N	\N	\N	\N	\N	\N	\N
2054	bad	bad	me@bad1.com	\N	\N	\N	\N	\N	\N	\N	\N	CA	\N	\N	\N	\N	\N	\N	\N
2121	able	945	able945@mailinator.com									SE	\N					\N	
2358	able	949	able949@mailinator.com	alt@email.com	third@email.com	address	city	post	address	city	post	SE	SE	949 Org	+1 (444) 		+1 (444) 4	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2503												\N	\N					\N	
2170	able	947	able947@mailinator.com	alt@email.com		address	city	post				SE	\N	947 Org	+1 (333) 		+1 (555) 5	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2207	able	947	able947@mailinator.com	alt@email.com		address	city	post				SE	\N	947 Org	+1 (333) 		+1 (555) 5	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2504												\N	\N					\N	
2516	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2518	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2521												\N	\N					\N	
2523												\N	\N					\N	
2219	able	948	able948@mailinator.com			address	city	post				SE	\N		+1 (555) 		+1 (666) 77	\N	
2368	able	950	able950@mailinator.com			address	city	post				CA	\N		+1 (111) 		+1 (111) 1	\N	
2390	able	949	able949@mailinator.com	alt@email.com	third@email.com	address	city	post	address	city	post	SE	SE	949 Org	+1 (444) 		+1 (444) 4	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	
2426	John	Smith	primary@email.com	alternate@email.com	third@email.com	\N	\N	\N	\N	\N	\N	\N	\N	Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2528	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2530	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2533												\N	\N					\N	
2535												\N	\N					\N	
2540	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2542	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2545												\N	\N					\N	
2547												\N	\N					\N	
2552	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2554	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2557												\N	\N					\N	
2559												\N	\N					\N	
2567	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2569	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2577												\N	\N					\N	
2579												\N	\N					\N	
2572	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2574	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2583												\N	\N					\N	
2585												\N	\N					\N	
2589	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2591	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2594												\N	\N					\N	
2596												\N	\N					\N	
2732												\N	\N					\N	
2734												\N	\N					\N	
2743	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2745	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2748												\N	\N					\N	
2750												\N	\N					\N	
2655	able	951	able951@mailinator.com			address	city	post				SE	\N		+1 (222) 		+1 (222) 2	\N	
2669	able	951	able951@mailinator.com			address	city	post				SE	\N		+1 (222) 		+1 (222) 2	\N	
2692	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2694	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2697												\N	\N					\N	
2699												\N	\N					\N	
2703	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2705	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2708												\N	\N					\N	
2710												\N	\N					\N	
2715	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2717	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2720												\N	\N					\N	
2722												\N	\N					\N	
2727	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2729	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2755	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2757	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2760												\N	\N					\N	
2762												\N	\N					\N	
2784	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2786	John	Smith	primary@email.com	alternate@email.com	third@email.com	200 Adelaide Street West, Suite 100	Toronto	M5H 1W7	200 Bloor Street West	Toronto	M5R 2T1	CA	US	#1 Imported Organization	+1 416 762 0032	123	+1 416 762 0033	PUBLIC_HEALTH_ORGANIZATION	Other
2789												\N	\N					\N	
2791												\N	\N					\N	
\.


--
-- Data for Name: application; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY application (application_id, username, street, city, country, landline_extension, full_name, landline_number, snomedlicense, type, subtype, email, alternate_email, third_email, mobile_number, organization_name, organization_type, billing_street, billing_city, billing_country, other_text, billing_post_code, post_code, organization_type_other, submitted_at, notes_internal, completed_at, approval_state, commercial_usage_id, affiliate_details_id, member_id, application_type, reason, affiliate_id) FROM stdin;
2714	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2716	2715	1438	PRIMARY	\N	2718
2719	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2721	2720	1438	PRIMARY	\N	2723
2132	able946@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	VENDOR	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-05 17:13:48.961-04		2014-08-05 17:14:17.999-04	APPROVED	2133	2131	1438	PRIMARY	\N	2130
2754	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2756	2755	1438	PRIMARY	\N	2758
2759	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2761	2760	1438	PRIMARY	\N	2763
2298	able949@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	VENDOR	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-06 14:46:05.077-04		2014-08-08 09:33:25.767-04	APPROVED	2299	2297	1438	PRIMARY	\N	2296
703	able921@mailinator.com	\N	\N	Canada	\N	able 921	\N	f	COMMERCIAL	\N	able921@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2014-07-15 16:51:22.326259-04	\N	\N	NOT_SUBMITTED	1021	\N	1437	PRIMARY	\N	704
118	able918@mailinator.com	\N	\N	\N	\N	able 918	+1 417 762 0032	t	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2014-07-15 16:51:22.326259-04	\N	2014-07-15 16:51:22.326259-04	APPROVED	1021	\N	1437	PRIMARY	\N	119
137	able919@mailinator.com	\N	\N	\N	\N	able 919	+1 416 762 0032	t	COMMERCIAL	DEVELOPER	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2014-07-15 16:51:22.326259-04	\N	2014-07-15 16:51:22.326259-04	APPROVED	1021	\N	1437	PRIMARY	\N	139
1200	able926@mailinator.com	12123	city	Canada		able 926	+1 23	t	COMMERCIAL	VENDOR	able926@mailinator.com	email2@a.com	email3@a.com	+2	able 926 ORG	HEALTHERCARE_APPLICATION_DEVELOPER	12123	city	Canada		postal	postal		2014-07-18 16:27:02.611-04	greate notes!	2014-07-18 16:30:23.169-04	APPROVED	1202	\N	1437	PRIMARY	\N	1201
1425	able934@mailinator.com	111	111	Canada		able 934	+1 (111) 	t	COMMERCIAL	VENDOR	able934@mailinator.com	1@1.111	2@2.2222	+1 (111) 	111	HEALTHERCARE_APPLICATION_DEVELOPER	111	111	Canada		111	111		2014-07-23 16:31:30.443-04	ouoeuou	2014-07-23 16:31:58.952-04	APPROVED	1427	\N	1437	PRIMARY	\N	1426
732	able922@mailinator.com	\N	\N	Anguilla	\N	\N	\N	f	COMMERCIAL	\N	able922@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2014-07-15 16:51:22.326259-04	\N	\N	NOT_SUBMITTED	1021	\N	1437	PRIMARY	\N	733
1304	able931@intelliware.ca	\N	\N	Canada	\N	able 931	\N	f	COMMERCIAL	\N	able931@intelliware.ca	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	1306	\N	1437	PRIMARY	\N	1305
2456	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	\N	2457	1438	PRIMARY	\N	2458
2459	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	\N	2460	1438	PRIMARY	\N	2461
2515	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2517	2516	1438	PRIMARY	\N	2519
2520	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2522	2521	1438	PRIMARY	\N	2524
2551	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2553	2552	1438	PRIMARY	\N	2555
2556	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2558	2557	1438	PRIMARY	\N	2560
2656	able951@mailinator.com	\N	\N	\N	\N	\N	\N	t	INDIVIDUAL	PERSONAL	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-12 15:20:01.563-04		2014-08-12 15:20:36.419-04	APPROVED	2657	2655	1438	PRIMARY	\N	2654
2161	able946@mailinator.com	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	\N	\N	1438	EXTENSION	\N	\N
305	able920@mailinator.com	200 adelaide	Toronto	Canada		able 920	+1 416 762 0032	t	COMMERCIAL	DEVELOPER	able920@mailinator.com	a@b.com	a@b2.com	+1 2389239082903	My Organization	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	200 adelaide	Toronto	Canada		123456	123456		2014-07-16 15:18:06.599-04	hello there	2014-07-16 17:21:13.637-04	CHANGE_REQUESTED	1021	\N	1437	PRIMARY	\N	306
1277	able931@mailinator.com	1	1	Ascension		able 931	+1	t	COMMERCIAL	DEVELOPER	able931@mailinator.com	a@a.com	b@b.com	+1	931 Organization five again more2	PRIVATE_HEALTH_ORGANIZATION	1	1	Ascension		1	1		2014-07-21 17:01:19.054-04	STAFF NOTES! Again... 2	2014-07-21 17:04:21.384-04	APPROVED	1279	\N	1437	PRIMARY	\N	1278
1383	able933@mailinator.com	ou	oeu	Canada		able 933	+1 (278) 	t	COMMERCIAL	DEVELOPER	able933@mailinator.com	a@a.com	a@b.com	+1 (898) 9	933 org	PUBLIC_HEALTH_ORGANIZATION	ou	oeu	Canada		oeu	oeu		2014-07-23 12:30:03.905-04	staff notes\nFrom admin...	\N	CHANGE_REQUESTED	1385	\N	1437	PRIMARY	\N	1384
1208	able927@mailinator.com	\N	\N	Canada	\N	able 927	\N	f	COMMERCIAL	\N	able927@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	1210	\N	1437	PRIMARY	\N	1209
1254	able928@mailinator.com	\N	\N	Canada	\N	able 928	\N	f	COMMERCIAL	\N	able928@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	1256	\N	1437	PRIMARY	\N	1255
1263	able929@mailinator.com	\N	\N	Canada	\N	able 929	\N	f	COMMERCIAL	\N	able929@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	1265	\N	1437	PRIMARY	\N	1264
1269	able930@mailinator.com	\N	\N	Comoros	\N	able 930	\N	f	COMMERCIAL	\N	able930@mailinator.com	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	1271	\N	1437	PRIMARY	\N	1270
1337	able932@mailinator.com	1	1	Canada		able 932	+1 (1	t	COMMERCIAL	DEVELOPER	able932@mailinator.com	a@a.com	a@b.com	+1 (1	932 Organization 2222 requested ILLEGAL	RESEARCH_AND_DEVELOPMENT_ORGANIZATION	1	1	1		1	1		2014-07-21 17:27:21.718-04	notes 1	2014-07-21 17:06:12.863-04	CHANGE_REQUESTED	1339	\N	1437	PRIMARY	\N	1338
1163	able925@mailinator.com	123213 number 2	213123213	123123123		able 925	+1 234	t	COMMERCIAL	DEVELOPER	able925@mailinator.com	email2@a.com	email3@a.com	+1 234	able925 org	PUBLIC_HEALTH_ORGANIZATION	123213	213123213	123123123		12312123	12312123		2014-07-18 15:07:38.183-04	some note 2 234 ou again more again and much more again ....oeee more moo134	2014-07-18 15:07:01.315-04	NOT_SUBMITTED	1165	\N	1437	PRIMARY	\N	1164
1839	able941@mailinator.com	\N	\N	\N	\N	\N	\N	t	ACADEMIC	DEVELOPMENT	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-31 15:33:52.305-04		2014-07-31 15:34:44.306-04	APPROVED	1841	1838	1438	PRIMARY	\N	1840
2467	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	\N	2468	1438	PRIMARY	\N	2469
2527	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2529	2528	1438	PRIMARY	\N	2531
2532	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2534	2533	1438	PRIMARY	\N	2536
2566	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2568	2567	1438	PRIMARY	\N	2570
1859	able942@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	VENDOR	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-31 15:36:54.192-04	sweden	2014-07-31 15:37:22.043-04	APPROVED	1861	1858	1438	PRIMARY	\N	1860
1570	able938@mailinator.com	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	DEVELOPER	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-29 16:24:31.085-04	blah\nFrom admin non-ihtsdo...	\N	CHANGE_REQUESTED	1572	1569	1438	PRIMARY	\N	1571
1440	able935@mailinator.com	street	city	Congo, (Congo Brazzaville)		able 935	+1 (111) 1	t	COMMERCIAL	DEVELOPER	able935@mailinator.com	2@a.com	3@a.com	+1 (111) 	935 Organization	PUBLIC_HEALTH_ORGANIZATION	billingstreet	billing city	France		billing postalcode	postalcode		2014-07-25 11:16:10.321-04	some staff notes	2014-07-25 11:16:32.631-04	APPROVED	1442	\N	1437	PRIMARY	\N	1441
1018	able923@mailinator.com	1	1	Canada		ableyy 923	+1 213 4545	t	COMMERCIAL	DEVELOPER	able923@mailinator.com	923@com	2@com	+1 222	923 organazitaion	PUBLIC_HEALTH_ORGANIZATION	1	1	Canada		1	1		2014-07-23 12:18:34.17-04	new set of staff notes hello more	2014-07-23 12:19:38.355-04	APPROVED	1021	\N	1437	PRIMARY	\N	1019
2576	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2578	2577	1438	PRIMARY	\N	2580
2171	able947@mailinator.com	\N	\N	\N	\N	\N	\N	t	ACADEMIC	HEALTHCAREPROVIDER	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-06 10:03:08.41-04	super note!	2014-08-06 10:03:24.179-04	APPROVED	2172	2170	1438	PRIMARY	\N	2169
2588	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2590	2589	1438	PRIMARY	\N	2592
2593	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2595	2594	1438	PRIMARY	\N	2597
2369	able950@mailinator.com	\N	\N	\N	\N	\N	\N	t	INDIVIDUAL	PERSONAL	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-08 10:25:22.963-04		2014-08-08 10:41:30.125-04	REJECTED	2370	2368	1437	PRIMARY	\N	2367
2425	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	\N	2426	1438	PRIMARY	\N	2427
2428	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	\N	2429	1438	PRIMARY	\N	2430
2464	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	\N	2465	1438	PRIMARY	\N	2466
2691	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2693	2692	1438	PRIMARY	\N	2695
2696	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2698	2697	1438	PRIMARY	\N	2700
2702	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2704	2703	1438	PRIMARY	\N	2706
2707	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2709	2708	1438	PRIMARY	\N	2711
2726	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2728	2727	1438	PRIMARY	\N	2730
2731	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2733	2732	1438	PRIMARY	\N	2735
2783	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2785	2784	1438	PRIMARY	\N	2787
2788	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2790	2789	1438	PRIMARY	\N	2792
2122	able945@mailinator.com	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	2123	2121	1438	PRIMARY	\N	2120
2742	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2744	2743	1438	PRIMARY	\N	2746
2747	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2749	2748	1438	PRIMARY	\N	2751
2220	able948@mailinator.com	\N	\N	\N	\N	\N	\N	t	INDIVIDUAL	PERSONAL	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-08-06 10:40:32.089-04	sweden note	2014-08-06 10:41:09.811-04	APPROVED	2221	2219	1438	PRIMARY	\N	2218
1732	able939@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	VENDOR	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-31 14:58:41.329-04	sweden note\nihtsdo note	2014-07-31 15:02:14.808-04	APPROVED	1734	1731	1437	PRIMARY	\N	1733
1494	able936@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	DEVELOPER	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-28 11:29:32.995-04	my notes	2014-07-31 10:25:51.231-04	APPROVED	1496	1493	1437	PRIMARY	\N	1495
1819	able940@mailinator.com	\N	\N	\N	\N	\N	\N	t	INDIVIDUAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-31 15:18:27.63-04		2014-07-31 15:18:59.973-04	APPROVED	1821	1818	1438	PRIMARY	\N	1820
1513	able937@mailinator.com	\N	\N	\N	\N	\N	\N	t	COMMERCIAL	DEVELOPER	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	2014-07-28 16:23:54.875-04	my notes	2014-07-28 16:24:39.435-04	CHANGE_REQUESTED	1515	1512	1437	PRIMARY	\N	1514
2010	able943@mailinator.com	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	2011	2009	1437	PRIMARY	\N	2008
2035	able944@mailinator.com	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	2036	2034	1437	PRIMARY	\N	2033
2448	\N	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	\N	2449	1438	PRIMARY	\N	2450
2055	me@bad1.com	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	NOT_SUBMITTED	2056	2054	1437	PRIMARY	\N	2053
2451	\N	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	\N	2452	1438	PRIMARY	\N	2453
2498	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	\N	2499	1438	PRIMARY	\N	2501
2502	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	\N	2503	1438	PRIMARY	\N	2505
2539	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2541	2540	1438	PRIMARY	\N	2543
2544	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2546	2545	1438	PRIMARY	\N	2548
2571	\N	\N	\N	\N	\N	\N	\N	f	COMMERCIAL	RESEARCH	\N	\N	\N	\N	\N	\N	\N	\N	\N	Other Text	\N	\N	\N	\N	\N	\N	APPROVED	2573	2572	1438	PRIMARY	\N	2575
2582	\N	\N	\N	\N	\N	\N	\N	f	ACADEMIC	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N		\N	\N	\N	\N	\N	\N	APPROVED	2584	2583	1438	PRIMARY	\N	2586
2389	able949@mailinator.com	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	2014-08-12 15:12:11.038-04		\N	RESUBMITTED	\N	2390	2308	EXTENSION	hello!	2296
\.


--
-- Data for Name: commercial_usage; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY commercial_usage (commercial_usage_id, created, key, start_date, end_date, note, approval_state, submitted, affiliate_id, current_usage, planned_usage, purpose, agreement_type, type, implementation_status) FROM stdin;
1339	2014-07-21 17:02:11.708-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1338	current 2	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	DEVELOPMENT
1572	2014-07-29 15:48:40.834-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1571	current	planned	purpose	AFFILIATE_PUBLIC_GOOD	COMMERCIAL	DEVELOPMENT
1021	2014-07-09 12:25:57.131-04	\N	2014-01-01	2014-06-30	\N	NOT_SUBMITTED	\N	1019	current	planned	puprose	AFFILIATE_NORMAL	COMMERCIAL	DEVELOPMENT
1210	2014-07-18 15:51:36.287-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1209	\N	\N	\N	\N	COMMERCIAL	\N
1202	2014-07-18 15:40:44.201-04	\N	2014-07-01	2014-12-31	\N	SUBMITTED	2014-07-18 16:28:13.789-04	1201	current	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	IMPLEMENTED
289	2014-07-03 10:58:48.604-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	139	u	ou	ou	AFFILIATE_NORMAL	COMMERCIAL	\N
705	2014-07-07 13:42:10.168-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	704	\N	\N	\N	\N	COMMERCIAL	\N
734	2014-07-07 14:25:06.064-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	733	\N	\N	\N	\N	COMMERCIAL	\N
20	2014-06-24 14:26:33.851-04	\N	2014-01-09	2014-01-20	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
21	2014-06-24 14:39:37.332-04	\N	2014-06-30	2014-12-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
33	2014-06-24 16:02:30.932-04	\N	2012-12-31	2013-07-29	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
37	2014-06-24 16:04:31.191-04	\N	2014-06-30	2014-12-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
42	2014-06-24 17:03:25.973-04	\N	2014-01-01	2014-06-29	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
43	2014-06-24 17:05:23.119-04	\N	2014-06-30	2014-12-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
1256	2014-07-21 11:45:46.933-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1255	\N	\N	\N	\N	COMMERCIAL	\N
44	2014-06-25 14:32:21.203-04	\N	2013-06-30	2013-12-31	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
45	2014-06-25 14:41:33.789-04	\N	2012-01-01	2012-06-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
47	2014-06-25 14:49:33.929-04	\N	2012-07-01	2012-12-31	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
1265	2014-07-21 11:53:24.372-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1264	\N	\N	\N	\N	COMMERCIAL	\N
76	2014-06-26 10:42:02.388-04	\N	2013-07-01	2013-12-31	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
1271	2014-07-21 12:10:21.215-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1270	\N	\N	\N	\N	COMMERCIAL	\N
310	2014-07-03 11:50:14.249-04	\N	2014-01-01	2014-06-30	\N	NOT_SUBMITTED	\N	306	current usage 2	ou	hallp	AFFILIATE_NORMAL	COMMERCIAL	\N
329	2014-07-03 11:50:14.249-04	\N	2013-07-01	2013-12-31	\N	NOT_SUBMITTED	\N	306	current usage	planned usage	purpose	AFFILIATE_NORMAL	ACADEMIC	\N
340	2014-07-03 11:50:14.249-04	\N	2013-01-01	2013-06-30	\N	NOT_SUBMITTED	\N	306	current usage	2planned usage	purpose	AFFILIATE_NORMAL	COMMERCIAL	\N
1292	2014-07-18 15:40:44.201-04	\N	2014-01-01	2014-06-30	\N	NOT_SUBMITTED	\N	1201	current	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	IMPLEMENTED
1010	2014-07-03 11:50:14.249-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	306	current usage 2	ououoeuoeu 222	hallp	AFFILIATE_NORMAL	COMMERCIAL	\N
1020	2014-07-09 12:25:57.131-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1019	current 923	planned	puprose	AFFILIATE_NORMAL	COMMERCIAL	\N
1841	2014-07-31 15:32:51.384-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1840	current	planned	purpose	AFFILIATE_RESEARCH	ACADEMIC	DEVELOPMENT
1496	2014-07-28 11:26:30.003-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1495	current usage	planned usage	purpose	AFFILIATE_NORMAL	COMMERCIAL	IMPLEMENTED
1385	2014-07-23 12:28:34.381-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1384	ou	ou	ou	AFFILIATE_RESEARCH	COMMERCIAL	DEVELOPMENT
1306	2014-07-21 16:22:38.04-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1305	\N	\N	\N	\N	COMMERCIAL	\N
79	2014-06-26 11:38:56.671-04	\N	2014-01-01	2014-06-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
31	2014-06-24 16:00:31.865-04	\N	2014-06-30	2014-12-30	\N	SUBMITTED	2014-06-26 13:30:20.101-04	\N	\N	\N	\N	\N	COMMERCIAL	\N
125	2014-06-26 16:04:40.936-04	\N	2014-01-01	2014-06-30	\N	SUBMITTED	2014-06-26 16:09:22.205-04	119	\N	\N	\N	\N	COMMERCIAL	\N
127	2014-06-26 16:09:32.175-04	\N	2013-07-01	2013-12-31	\N	NOT_SUBMITTED	\N	119	\N	\N	\N	\N	COMMERCIAL	\N
144	2014-06-26 16:41:25.144-04	\N	2013-07-01	2013-12-31	\N	SUBMITTED	2014-06-26 17:02:30.514-04	\N	\N	\N	\N	\N	COMMERCIAL	\N
1165	2014-07-18 13:13:25.533-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1164	current usage MORE 99	planned usage	purpose	AFFILIATE_NORMAL	COMMERCIAL	\N
153	2014-06-26 16:27:17.158-04	\N	2013-01-01	2013-06-30	\N	NOT_SUBMITTED	\N	\N	\N	\N	\N	\N	COMMERCIAL	\N
140	2014-06-26 16:27:17.158-04	\N	2014-01-01	2014-06-30	\N	SUBMITTED	2014-06-27 11:30:53.38-04	\N	\N	\N	\N	\N	COMMERCIAL	\N
158	2014-06-26 16:27:17.158-04	\N	2012-07-01	2012-12-31	\N	NOT_SUBMITTED	2014-06-27 11:30:53.38-04	\N	\N	\N	\N	\N	COMMERCIAL	\N
2056	2014-08-05 14:43:46.634-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2053	\N	\N	\N	\N	COMMERCIAL	\N
1279	2014-07-21 12:24:24.146-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1278	current five	planned five	purpose five	AFFILIATE_NORMAL	COMMERCIAL	DEVELOPMENT
2123	2014-08-05 16:18:05.854-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2120	\N	\N	\N	\N	COMMERCIAL	\N
1427	2014-07-23 16:30:44.491-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1426	ou	ou	ou	AFFILIATE_NORMAL	COMMERCIAL	IMPLEMENTED
1821	2014-07-31 15:16:42.467-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1820	usage	planned	purpsoe	AFFILIATE_NORMAL	INDIVIDUAL	IMPLEMENTED
1515	2014-07-28 16:21:45.789-04	\N	2014-07-01	2014-12-31	\N	SUBMITTED	2014-07-29 14:37:24.463-04	1514	current	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	DEVELOPMENT
2133	2014-08-05 17:11:50.296-04	\N	2014-07-01	2014-12-31	\N	SUBMITTED	2014-08-06 09:59:20.093-04	2130	current	planned	purpose	AFFILIATE_NORMAL	COMMERCIAL	IMPLEMENTED
1861	2014-07-31 15:35:39.955-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1860	current	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	DEVELOPMENT
1442	2014-07-25 10:39:07.073-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	1441	current	planned	purpose	AFFILIATE_NORMAL	COMMERCIAL	DEVELOPMENT
1734	2014-07-31 12:03:47.396-04	\N	2014-07-01	2014-12-31	\N	SUBMITTED	2014-08-01 16:13:56.77-04	1733	current	planned	purpose	AFFILIATE_RESEARCH	COMMERCIAL	DEVELOPMENT
2155	2014-08-05 17:11:50.296-04	\N	2014-01-01	2014-06-30	\N	NOT_SUBMITTED	\N	2130	current	planned	purpose	AFFILIATE_NORMAL	COMMERCIAL	IMPLEMENTED
2011	2014-08-05 13:57:02.339-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2008	\N	\N	\N	\N	COMMERCIAL	\N
2036	2014-08-05 14:32:03.208-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2033	\N	\N	\N	\N	COMMERCIAL	\N
2299	2014-08-06 13:19:32.078-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2296	current	planned	purpose	AFFILIATE_NORMAL	COMMERCIAL	DEVELOPMENT
2172	2014-08-06 10:01:10.87-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2169	current new	planned	purpose	AFFILIATE_NORMAL	ACADEMIC	IMPLEMENTED
2370	2014-08-08 10:22:38.638-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2367	current	planned	purpose	AFFILIATE_NORMAL	INDIVIDUAL	DEVELOPMENT
2221	2014-08-06 10:36:52.564-04	\N	2014-07-01	2014-12-31	\N	SUBMITTED	2014-08-06 10:55:01.266-04	2218	current CHANGED	planned	purpose	AFFILIATE_PUBLIC_GOOD	INDIVIDUAL	DEVELOPMENT
2517	2014-08-11 16:06:25.278-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2519	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2522	2014-08-11 16:07:21.956-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2524				\N	ACADEMIC	
2529	2014-08-11 16:24:09.555-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2531	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2534	2014-08-11 16:24:10.463-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2536				\N	ACADEMIC	
2541	2014-08-11 16:30:11.366-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2543	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2546	2014-08-11 16:30:11.387-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2548				\N	ACADEMIC	
2553	2014-08-11 16:51:53.084-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2555	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2558	2014-08-11 16:51:53.102-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2560				\N	ACADEMIC	
2568	2014-08-12 11:13:55.223-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2570	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2578	2014-08-12 11:13:59.26-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2580				\N	ACADEMIC	
2573	2014-08-12 11:13:57.553-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2575	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2584	2014-08-12 11:14:00.754-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2586				\N	ACADEMIC	
2590	2014-08-12 11:14:16.673-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2592	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2595	2014-08-12 11:14:17.41-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2597				\N	ACADEMIC	
2657	2014-08-12 15:19:04.859-04	\N	2014-07-01	2014-12-31	\N	NOT_SUBMITTED	\N	2654	current	planned	purpose	AFFILIATE_RESEARCH	INDIVIDUAL	PLANNING
2693	2014-08-13 12:29:11.437-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2695	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2698	2014-08-13 12:29:11.456-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2700				\N	ACADEMIC	
2704	2014-08-13 12:30:05.015-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2706	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2709	2014-08-13 12:30:05.023-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2711				\N	ACADEMIC	
2716	2014-08-13 12:36:40.353-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2718	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2721	2014-08-13 12:36:40.373-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2723				\N	ACADEMIC	
2728	2014-08-13 12:38:46.005-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2730	My current usage	My planned usage	My purpose	AFFILIATE_NORMAL	COMMERCIAL	Implementation Status
2733	2014-08-13 12:38:46.027-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2735				\N	ACADEMIC	
2744	2014-08-13 15:05:29.182-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2746	\N	\N	\N	\N	COMMERCIAL	\N
2749	2014-08-13 15:05:29.201-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2751	\N	\N	\N	\N	ACADEMIC	\N
2756	2014-08-13 16:24:32.145-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2758	\N	\N	\N	\N	COMMERCIAL	\N
2761	2014-08-13 16:24:32.161-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2763	\N	\N	\N	\N	ACADEMIC	\N
2785	2014-08-14 11:04:47.72-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2787	\N	\N	\N	\N	COMMERCIAL	\N
2790	2014-08-14 11:04:47.75-04	\N	2014-07-01	2014-12-31	\N	APPROVED	\N	2792	\N	\N	\N	\N	ACADEMIC	\N
\.


--
-- Data for Name: commercial_usage_count; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY commercial_usage_count (commercial_usage_count_id, commercial_usage_id, created, practices, country_iso_code_2) FROM stdin;
1011	1010	2014-07-08 13:35:11.354-04	0	AL
341	340	2014-07-04 13:38:45.682-04	0	WF
998	310	2014-07-08 13:35:11.352-04	0	AX
344	340	2014-07-04 13:51:11.132-04	3	CC
1012	1010	2014-07-08 13:35:11.352-04	0	AX
1013	1010	2014-07-04 11:10:07.779-04	0	VN
1014	1010	2014-07-04 11:10:23.363-04	0	UM
2176	2172	2014-08-06 10:02:15.153-04	0	AX
1098	1021	2014-07-15 13:49:52.895-04	1	AL
1095	1020	2014-07-15 17:03:26.629-04	20	DZ
1205	1202	2014-07-18 15:49:13.024-04	0	AL
1446	1442	2014-07-25 10:40:21.217-04	0	AL
2183	2172	2014-08-06 10:02:15.203-04	0	AI
1556	1515	2014-07-29 14:36:22.071-04	2	AC
1749	1734	2014-07-31 13:22:53.657-04	0	AX
2137	2133	2014-08-05 17:13:22.121-04	3	AX
2189	2172	2014-08-06 10:02:15.239-04	0	AU
2225	2221	2014-08-06 10:38:29.215-04	0	AF
2663	2657	2014-08-12 15:19:56.731-04	0	AL
333	329	2014-07-03 12:35:03.42-04	0	AF
334	329	2014-07-03 12:59:19.718-04	0	ZW
342	340	2014-07-03 12:59:19.718-04	0	ZW
346	340	2014-07-03 13:02:25.108-04	0	BB
290	289	2014-07-03 10:59:08.466-04	7	AF
393	340	2014-07-04 10:48:53.696-04	0	AS
395	340	2014-07-04 10:49:55.985-04	0	AO
397	340	2014-07-04 10:50:27.848-04	0	AD
398	340	2014-07-04 11:08:32.854-04	0	DZ
999	310	2014-07-08 13:35:11.354-04	0	AL
2177	2172	2014-08-06 10:02:15.152-04	0	AF
1094	1020	2014-07-15 13:43:09.276-04	1	AX
1293	1292	2014-07-18 15:49:13.024-04	0	AL
1447	1442	2014-07-25 10:40:21.233-04	0	DZ
1557	1515	2014-07-29 14:36:16.972-04	0	AU
2182	2172	2014-08-06 10:02:15.191-04	0	AO
1750	1734	2014-08-01 16:13:40.624-04	5	AF
2188	2172	2014-08-06 10:02:15.227-04	0	AC
2138	2133	2014-08-06 09:59:11.914-04	7888	AL
2241	2221	2014-08-06 10:40:29.194-04	0	AX
644	310	2014-07-04 11:10:23.363-04	0	UM
664	340	2014-07-04 13:07:00.099-04	0	AW
666	340	2014-07-04 13:09:46.802-04	0	AG
665	340	2014-07-07 13:01:02.924-04	5	AX
1026	329	2014-07-09 16:52:43.872-04	0	AL
1096	1020	2014-07-15 13:43:05.355-04	4	AF
2178	2172	2014-08-06 10:02:15.156-04	0	AL
1323	1279	2014-07-21 16:56:23.305-04	2	FR
1500	1496	2014-07-28 11:29:01.24-04	0	DZ
1584	1572	2014-07-29 16:21:42.687-04	1	AF
2153	2133	2014-08-06 09:58:50.004-04	0	AD
2156	2155	2014-08-06 09:58:50.004-04	0	AD
2157	2155	2014-08-06 09:59:11.914-04	7888	AL
2158	2155	2014-08-05 17:13:22.121-04	3	AX
2185	2172	2014-08-06 10:02:15.203-04	0	AR
2375	2370	2014-08-08 10:25:16.959-04	0	AF
668	340	2014-07-04 13:17:45.422-04	0	AR
1097	1020	2014-07-15 13:42:59.165-04	0	AL
1342	1339	2014-07-21 17:03:31.95-04	1	AL
1353	1339	2014-07-21 17:05:22.251-04	0	AF
2179	2172	2014-08-06 10:02:15.159-04	0	AS
1501	1496	2014-07-28 11:29:05.693-04	3	AX
1585	1572	2014-07-29 16:21:49.872-04	3	AL
2184	2172	2014-08-06 10:02:15.203-04	0	AG
2190	2172	2014-08-06 10:02:15.243-04	0	AQ
2374	2370	2014-08-08 10:25:16.959-04	0	AX
670	340	2014-07-04 13:51:17.064-04	0	AL
671	340	2014-07-07 15:06:10.358-04	2	AF
2180	2172	2014-08-06 10:02:15.158-04	0	DZ
1169	1165	2014-07-18 13:16:27.707-04	4	AF
2186	2172	2014-08-06 10:02:15.203-04	0	AM
1168	1165	2014-07-18 15:01:47.394-04	3234	AL
1445	1442	2014-07-25 10:40:21.213-04	0	AX
2661	2657	2014-08-12 15:19:56.729-04	0	AF
1502	1496	2014-07-28 11:29:09.073-04	1	AL
1586	1572	2014-07-29 16:21:45.97-04	2	AX
1170	1165	2014-07-18 13:16:32.119-04	99	AX
1444	1442	2014-07-25 10:40:21.211-04	0	AF
1555	1515	2014-07-29 14:36:16.972-04	0	AQ
2181	2172	2014-08-06 10:02:15.164-04	0	AD
1587	1572	2014-07-29 16:21:54.354-04	4	DZ
2187	2172	2014-08-06 10:02:15.21-04	0	AW
2662	2657	2014-08-12 15:19:56.729-04	0	AX
638	310	2014-07-04 11:10:07.779-04	0	VN
649	340	2014-07-04 11:17:27.07-04	0	AI
\.


--
-- Data for Name: commercial_usage_entry; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY commercial_usage_entry (commercial_usage_entry_id, created, name, start_date, end_date, country_iso_code_2, commercial_usage_id, note) FROM stdin;
1005	2014-07-09 10:34:37.886-04	afgan 1	2014-07-09	\N	AF	329	\N
700	2014-07-07 12:43:38.211-04	uei	2012-07-07	2012-08-01	AF	340	\N
1099	2014-07-15 14:06:56.861-04	ou	2014-07-17	\N	AF	1020	\N
1115	2014-07-16 14:37:27.325-04	able	2014-07-01	\N	AF	1020	\N
1116	2014-07-16 14:37:39.576-04	later	2014-05-22	\N	AF	1020	\N
1117	2014-07-16 14:37:52.089-04	ZZZZ	2014-05-14	\N	AF	1020	\N
1118	2014-07-16 14:47:14.994-04	Babel	2014-03-05	\N	AF	1020	\N
1171	2014-07-18 13:16:21.175-04	ouoeu	2014-07-02	\N	AF	1165	\N
1324	2014-07-21 16:56:42.856-04	france hosp 1	2014-06-30	\N	FR	1279	a hospital
1352	2014-07-21 17:05:20.106-04	alb host	2014-07-09	\N	AL	1339	\N
1558	2014-07-29 14:36:32.695-04	ascen	2014-06-03	\N	AC	1515	\N
1797	2014-07-31 14:30:04.526-04	hopital 2	2014-02-26	2014-07-25	AF	1734	\N
699	2014-07-07 12:24:28.516-04	alg 1	2014-07-07	2014-07-01	DZ	340	\N
1796	2014-07-31 14:29:51.73-04	hopital 1	2014-02-18	\N	AF	1734	some notes
701	2014-07-07 13:39:42.522-04	afg 1	2014-04-08	2015-01-16	AF	340	\N
2139	2014-08-05 17:13:32.433-04	hos	2014-01-17	\N	AX	2133	\N
2154	2014-08-06 09:59:01.253-04	andorra hos	2014-08-11	\N	AD	2133	\N
2159	2014-08-06 09:59:01.253-04	andorra hos	2014-08-11	\N	AD	2155	\N
2160	2014-08-05 17:13:32.433-04	hos	2014-01-17	\N	AX	2155	\N
\.


--
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY country (iso_code_2, iso_code_3, common_name, exclude_registration, alternate_registration_url, member_id) FROM stdin;
ZZ	ZZZ	Z Land	f	\N	1438
ZW	ZWE	Zimbabwe	f	\N	2310
AF	AFG	Afghanistan	f	\N	2308
FR	FRA	France	f	\N	2309
AU	AUS	Australia	t	https://nehta.org.au/aht/	1437
BE	BEL	Belgium	t	http://www.health.belgium.be/eportal	1437
BN	BRN	Brunei	t	http://www.moh.gov.bn/	1437
CA	CAN	Canada	t	http://sl.infoway-inforoute.ca/content/disppage.asp?cw_page=secure_snomedct_standards_e	1437
CL	CHL	Chile	t	http://www.ocis.cl/	1437
CZ	CZE	Czech Republic	t	http://www.ksrzis.cz/	1437
DK	DNK	Denmark	t	http://www.ssi.dk/snomedct	1437
EE	EST	Estonia	t	http://www.e-tervis.ee/index.php/en/	1437
US	USA	United States	t	http://www.nlm.nih.gov/	\N
GB	GBR	United Kingdom	t	http://systems.hscic.gov.uk/data/uktc	\N
AL	ALB	Albania	f	\N	1437
DZ	DZA	Algeria	f	\N	1437
AD	AND	Andorra	f	\N	1437
AO	AGO	Angola	f	\N	1437
AG	ATG	Antigua and Barbuda	f	\N	1437
AR	ARG	Argentina	f	\N	1437
AM	ARM	Armenia	f	\N	1437
AT	AUT	Austria	f	\N	1437
AZ	AZE	Azerbaijan	f	\N	1437
BS	BHS	Bahamas, The	f	\N	1437
BH	BHR	Bahrain	f	\N	1437
BD	BGD	Bangladesh	f	\N	1437
BB	BRB	Barbados	f	\N	1437
BY	BLR	Belarus	f	\N	1437
BZ	BLZ	Belize	f	\N	1437
BJ	BEN	Benin	f	\N	1437
BT	BTN	Bhutan	f	\N	1437
BO	BOL	Bolivia	f	\N	1437
BA	BIH	Bosnia and Herzegovina	f	\N	1437
BW	BWA	Botswana	f	\N	1437
BR	BRA	Brazil	f	\N	1437
BG	BGR	Bulgaria	f	\N	1437
BF	BFA	Burkina Faso	f	\N	1437
BI	BDI	Burundi	f	\N	1437
KH	KHM	Cambodia	f	\N	1437
CM	CMR	Cameroon	f	\N	1437
CV	CPV	Cape Verde	f	\N	1437
CF	CAF	Central African Republic	f	\N	1437
TD	TCD	Chad	f	\N	1437
CN	CHN	China, People's Republic of	f	\N	1437
CO	COL	Colombia	f	\N	1437
KM	COM	Comoros	f	\N	1437
CD	COD	Congo, (Congo Kinshasa)	f	\N	1437
CG	COG	Congo, (Congo Brazzaville)	f	\N	1437
CR	CRI	Costa Rica	f	\N	1437
CI	CIV	Cote d'Ivoire (Ivory Coast)	f	\N	1437
HR	HRV	Croatia	f	\N	1437
CU	CUB	Cuba	f	\N	1437
CY	CYP	Cyprus	f	\N	1437
DJ	DJI	Djibouti	f	\N	1437
DM	DMA	Dominica	f	\N	1437
DO	DOM	Dominican Republic	f	\N	1437
EC	ECU	Ecuador	f	\N	1437
EG	EGY	Egypt	f	\N	1437
SV	SLV	El Salvador	f	\N	1437
GQ	GNQ	Equatorial Guinea	f	\N	1437
ER	ERI	Eritrea	f	\N	1437
ET	ETH	Ethiopia	f	\N	1437
FJ	FJI	Fiji	f	\N	1437
FI	FIN	Finland	f	\N	1437
GA	GAB	Gabon	f	\N	1437
GM	GMB	Gambia, The	f	\N	1437
GE	GEO	Georgia	f	\N	1437
DE	DEU	Germany	f	\N	1437
GH	GHA	Ghana	f	\N	1437
GR	GRC	Greece	f	\N	1437
GD	GRD	Grenada	f	\N	1437
SY	SYR	Syria	f	\N	1437
TJ	TJK	Tajikistan	f	\N	1437
GT	GTM	Guatemala	f	\N	1437
GN	GIN	Guinea	f	\N	1437
GW	GNB	Guinea-Bissau	f	\N	1437
GY	GUY	Guyana	f	\N	1437
HT	HTI	Haiti	f	\N	1437
HN	HND	Honduras	f	\N	1437
HU	HUN	Hungary	f	\N	1437
ID	IDN	Indonesia	f	\N	1437
IR	IRN	Iran	f	\N	1437
IQ	IRQ	Iraq	f	\N	1437
IE	IRL	Ireland	f	\N	1437
IT	ITA	Italy	f	\N	1437
JM	JAM	Jamaica	f	\N	1437
JP	JPN	Japan	f	\N	1437
JO	JOR	Jordan	f	\N	1437
KZ	KAZ	Kazakhstan	f	\N	1437
KE	KEN	Kenya	f	\N	1437
KI	KIR	Kiribati	f	\N	1437
KP	PRK	Korea, North	f	\N	1437
KR	KOR	Korea, South	f	\N	1437
KW	KWT	Kuwait	f	\N	1437
KG	KGZ	Kyrgyzstan	f	\N	1437
LA	LAO	Laos	f	\N	1437
LV	LVA	Latvia	f	\N	1437
LB	LBN	Lebanon	f	\N	1437
LS	LSO	Lesotho	f	\N	1437
LR	LBR	Liberia	f	\N	1437
LY	LBY	Libya	f	\N	1437
LI	LIE	Liechtenstein	f	\N	1437
LU	LUX	Luxembourg	f	\N	1437
MK	MKD	Macedonia	f	\N	1437
MG	MDG	Madagascar	f	\N	1437
MW	MWI	Malawi	f	\N	1437
MV	MDV	Maldives	f	\N	1437
ML	MLI	Mali	f	\N	1437
MH	MHL	Marshall Islands	f	\N	1437
MR	MRT	Mauritania	f	\N	1437
MU	MUS	Mauritius	f	\N	1437
MX	MEX	Mexico	f	\N	1437
FM	FSM	Micronesia	f	\N	1437
MD	MDA	Moldova	f	\N	1437
MC	MCO	Monaco	f	\N	1437
MN	MNG	Mongolia	f	\N	1437
ME	MNE	Montenegro	f	\N	1437
MA	MAR	Morocco	f	\N	1437
MZ	MOZ	Mozambique	f	\N	1437
MM	MMR	Myanmar (Burma)	f	\N	1437
NA	NAM	Namibia	f	\N	1437
NR	NRU	Nauru	f	\N	1437
NP	NPL	Nepal	f	\N	1437
NI	NIC	Nicaragua	f	\N	1437
NE	NER	Niger	f	\N	1437
NG	NGA	Nigeria	f	\N	1437
NO	NOR	Norway	f	\N	1437
OM	OMN	Oman	f	\N	1437
PK	PAK	Pakistan	f	\N	1437
PW	PLW	Palau	f	\N	1437
PA	PAN	Panama	f	\N	1437
PG	PNG	Papua New Guinea	f	\N	1437
PY	PRY	Paraguay	f	\N	1437
PE	PER	Peru	f	\N	1437
PH	PHL	Philippines	f	\N	1437
QA	QAT	Qatar	f	\N	1437
RO	ROU	Romania	f	\N	1437
RU	RUS	Russia	f	\N	1437
RW	RWA	Rwanda	f	\N	1437
KN	KNA	Saint Kitts and Nevis	f	\N	1437
LC	LCA	Saint Lucia	f	\N	1437
VC	VCT	Saint Vincent and the Grenadines	f	\N	1437
WS	WSM	Samoa	f	\N	1437
SM	SMR	San Marino	f	\N	1437
ST	STP	Sao Tome and Principe	f	\N	1437
SA	SAU	Saudi Arabia	f	\N	1437
SN	SEN	Senegal	f	\N	1437
RS	SRB	Serbia	f	\N	1437
SC	SYC	Seychelles	f	\N	1437
SL	SLE	Sierra Leone	f	\N	1437
SB	SLB	Solomon Islands	f	\N	1437
SO	SOM	Somalia	f	\N	1437
ZA	ZAF	South Africa	f	\N	1437
LK	LKA	Sri Lanka	f	\N	1437
SD	SDN	Sudan	f	\N	1437
SR	SUR	Suriname	f	\N	1437
SZ	SWZ	Swaziland	f	\N	1437
CH	CHE	Switzerland	f	\N	1437
TZ	TZA	Tanzania	f	\N	1437
TH	THA	Thailand	f	\N	1437
TL	TLS	Timor-Leste (East Timor)	f	\N	1437
TG	TGO	Togo	f	\N	1437
TO	TON	Tonga	f	\N	1437
TT	TTO	Trinidad and Tobago	f	\N	1437
TN	TUN	Tunisia	f	\N	1437
TR	TUR	Turkey	f	\N	1437
TM	TKM	Turkmenistan	f	\N	1437
TV	TUV	Tuvalu	f	\N	1437
UG	UGA	Uganda	f	\N	1437
UA	UKR	Ukraine	f	\N	1437
AE	ARE	United Arab Emirates	f	\N	1437
UZ	UZB	Uzbekistan	f	\N	1437
VU	VUT	Vanuatu	f	\N	1437
VA	VAT	Vatican City	f	\N	1437
VE	VEN	Venezuela	f	\N	1437
VN	VNM	Vietnam	f	\N	1437
YE	YEM	Yemen	f	\N	1437
ZM	ZMB	Zambia	f	\N	1437
TW	TWN	China, Republic of (Taiwan)	f	\N	1437
CX	CXR	Christmas Island	f	\N	1437
CC	CCK	Cocos (Keeling) Islands	f	\N	1437
HM	HMD	Heard Island and McDonald Islands	f	\N	1437
NF	NFK	Norfolk Island	f	\N	1437
NC	NCL	New Caledonia	f	\N	1437
PF	PYF	French Polynesia	f	\N	1437
YT	MYT	Mayotte	f	\N	1437
PM	SPM	Saint Pierre and Miquelon	f	\N	1437
WF	WLF	Wallis and Futuna	f	\N	1437
TF	ATF	French Southern and Antarctic Lands	f	\N	1437
BV	BVT	Bouvet Island	f	\N	1437
CK	COK	Cook Islands	f	\N	1437
NU	NIU	Niue	f	\N	1437
TK	TKL	Tokelau	f	\N	1437
GG	GGY	Guernsey	f	\N	1437
IM	IMN	Isle of Man	f	\N	1437
JE	JEY	Jersey	f	\N	1437
AI	AIA	Anguilla	f	\N	1437
BM	BMU	Bermuda	f	\N	1437
IS	ISL	Iceland	t	http://www.landlaeknir.is/	1437
IO	IOT	British Indian Ocean Territory	f	\N	1437
VG	VGB	British Virgin Islands	f	\N	1437
KY	CYM	Cayman Islands	f	\N	1437
FK	FLK	Falkland Islands (Islas Malvinas)	f	\N	1437
GI	GIB	Gibraltar	f	\N	1437
MS	MSR	Montserrat	f	\N	1437
PN	PCN	Pitcairn Islands	f	\N	1437
SH	SHN	Saint Helena	f	\N	1437
GS	SGS	South Georgia & South Sandwich Islands	f	\N	1437
TC	TCA	Turks and Caicos Islands	f	\N	1437
MP	MNP	Northern Mariana Islands	f	\N	1437
PR	PRI	Puerto Rico	f	\N	1437
AS	ASM	American Samoa	f	\N	1437
GU	GUM	Guam	f	\N	1437
VI	VIR	U.S. Virgin Islands	f	\N	1437
UM	UMI	Wake Island	f	\N	1437
MO	MAC	Macau	f	\N	1437
FO	FRO	Faroe Islands	f	\N	1437
GL	GRL	Greenland	f	\N	1437
GF	GUF	French Guiana	f	\N	1437
GP	GLP	Guadeloupe	f	\N	1437
MQ	MTQ	Martinique	f	\N	1437
RE	REU	Reunion	f	\N	1437
AX	ALA	Aland	f	\N	1437
AW	ABW	Aruba	f	\N	1437
AN	ANT	Netherlands Antilles	f	\N	1437
SJ	SJM	Svalbard	f	\N	1437
AC	ASC	Ascension	f	\N	1437
TA	TAA	Tristan da Cunha	f	\N	1437
AQ	ATA	Australian Antarctic Territory	f	\N	1437
HK	HKG	Hong Kong	t	http://www.ehr.gov.hk/	1437
IN	IND	India	t	http://www.nic.in/	1437
IL	ISR	Israel	t	http://www.health.gov.il/English	1437
LT	LTU	Lithuania	t	http://www.vpc.lt/	1437
MY	MYS	Malaysia	t	http://www.moh.gov.my/	1437
MT	MLT	Malta	t	http://www.ehealth.gov.mt/	1437
NL	NLD	Netherlands	t	http://www.nictiz.nl/	1437
NZ	NZL	New Zealand	t	http://www.nzhis.govt.nz/moh.nsf/pagesns/497	1437
PL	POL	Poland	t	http://www.csioz.gov.pl/	1437
PT	PRT	Portugal	t	http://www.spms.pt/	1437
SG	SGP	Singapore	t	http://www.mohh.com.sg/	1437
SK	SVK	Slovakia	t	http://www.nczisk.sk/	1437
SI	SVN	Slovenia	t	http://www.mz.gov.si/en/	1437
ES	ESP	Spain	t	http://www.msps.es/profesionales/hcdsns/areaRecursosSem/snomed-ct/	1437
UY	URY	Uruguay	t	http://www.agesic.gub.uy/	1437
SE	SWE	Sweden	f	http://www.socialstyrelsen.se/facksprak	1438
\.


--
-- Data for Name: databasechangelog; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase) FROM stdin;
1	jhipster	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:08.548922-04	1	EXECUTED	7:f98c52e04a66d332ca4f201079a3bc29	createTable (x3), addPrimaryKey, createTable (x2), createIndex, addForeignKeyConstraint (x3), loadData (x3)		\N	3.1.1
2	jhipster	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:08.785258-04	2	EXECUTED	7:9aa8d2fb10a973c2eb73426937ab6c06	createTable (x2), addPrimaryKey, createIndex (x2), addForeignKeyConstraint		\N	3.1.1
4	jhipster	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.044417-04	3	EXECUTED	7:3079d166ac5118f2284dcc213246117e	addColumn, loadUpdateData, loadData		\N	3.1.1
MLDS-23-1	MB/DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.20165-04	4	EXECUTED	7:5b2b0ecc586cddeaecf101cb47b64acd	createTable, loadData	Downloaded from http://datahub.io/dataset/iso-3166-1-alpha-2-country-codes/resource/9c3b30dd-f5f3-4bbe-a3cb-d7b2c21d66ce.	\N	3.1.1
MLDS-23-2	MB/DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.284907-04	5	EXECUTED	7:cc67bc998038648d10405786c3bd9fda	createTable, addPrimaryKey		\N	3.1.1
MLDS-23-3	MB/DGL	none	2014-06-24 12:29:09.393083-04	6	EXECUTED	7:783b584bd49a941cfc9b6a2d583d4838	createTable, addPrimaryKey		\N	3.1.1
MLDS-23-4	MB/DGL	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.475827-04	7	EXECUTED	7:535eab0b1125f66efc0ace5d3f3b0e0c	createTable, addPrimaryKey		\N	3.1.1
MLDS-23-5	MB/DGL	none	2014-06-24 12:29:09.501735-04	8	EXECUTED	7:24f719bd7827ddc32c2dbfc29ce3e581	addForeignKeyConstraint (x3)		\N	3.1.1
MLDS-23-6	MB/DGL	none	2014-06-24 12:29:09.52595-04	9	EXECUTED	7:7327908386b837f9edc7573b05b19d7a	addColumn (x2)	Add date range to usage report	\N	3.1.1
MB	MLDS-23-7	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.551339-04	10	EXECUTED	7:118d4e4ddf14f81282dc742d001a09ec	dropTable, dropColumn, addColumn	Change entry mapping	\N	3.1.1
MB	MLDS-23-8	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 12:29:09.575711-04	11	EXECUTED	7:6d64c32c1ab5d78a31c07027bd6ccd75	dropTable	Delete obsolete jhipster table	\N	3.1.1
MLDS-01-1	MB	classpath:db-changelog.xml	2014-06-24 12:29:09.684697-04	12	EXECUTED	7:b48295eb3a1a77ee863067741f288c73	createSequence, createTable		\N	3.1.1
MLDS-02-1	MB	classpath:db-changelog.xml	2014-06-24 12:29:09.793261-04	13	EXECUTED	7:21b69dd19cdb39962960eda3ae215b11	createTable		\N	3.1.1
MLDS-04-1	AC	classpath:db-changelog.xml	2014-06-24 12:29:09.946973-04	14	EXECUTED	7:36e522d3cc33333139b64e01dac9de6a	addColumn (x10)		\N	3.1.1
MLDS-02-2-TOS	MB	classpath:db-changelog.xml	2014-06-24 12:29:10.061269-04	15	EXECUTED	7:e9177a2d1e003c6253fb5655ba09978c	createTable	Create an event log so we can record TOS acceptance	\N	3.1.1
MLDS-02-3	AC	classpath:db-changelog.xml	2014-06-24 12:29:10.176773-04	16	EXECUTED	7:a6e185c3da2aeedb13714cb6af42eb8b	createTable		\N	3.1.1
MLDS-04-3	AC	classpath:db-changelog.xml	2014-06-24 12:29:10.201693-04	17	EXECUTED	7:03f9173161dbc6d12eaf569c8f5fb8ab	addColumn		\N	3.1.1
MB	MLDS-23-9	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-24 16:03:55.62116-04	18	EXECUTED	7:75057c4ad188cecbef587a27187e19fa	addColumn	Add type to usage entry	\N	3.1.1
DGJ	MLDS-23-10	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 09:49:48.149545-04	19	EXECUTED	7:342e6b69263a5b1d4c1b373e48b0f612	addColumn	Add note to usage entry	\N	3.1.1
DGJ	MLDS-23-11	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 13:22:23.962944-04	20	EXECUTED	7:75abb7cda0825997301b87913b4eff2c	addColumn	Add note to commercial usage	\N	3.1.1
DGJ	MLDS-23-12	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 13:22:24.123319-04	21	EXECUTED	7:662e143baff31fc725feb6f8495c8dfa	addColumn	Add approval state to commercial usage	\N	3.1.1
DGJ	MLDS-23-13	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 14:41:16.339761-04	22	EXECUTED	7:f2897c69bf58a57372435a9ae954fb98	createTable, addPrimaryKey, addColumn, addForeignKeyConstraint	Introduce Licensee as owner of commercialUsageReports	\N	3.1.1
DGJ	MLDS-23-14	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 14:54:27.228-04	23	EXECUTED	7:a8532b925a53363907113f478a6cf6f2	addColumn	Add basic link from licensee to creator	\N	3.1.1
DGJ	MLDS-23-15	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-26 16:21:30.836684-04	24	EXECUTED	7:93e39244d53802d9d89a1d15070f8a08	addColumn, addForeignKeyConstraint	Add basic link from licensee to current application	\N	3.1.1
DGJ	MLDS-23-16	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-27 13:55:54.438253-04	25	EXECUTED	7:c5b1678a8501aadba9a687e1fad9500c	createTable, addPrimaryKey	Add Country Counts for Commercial Usage Report	\N	3.1.1
DGJ	MLDS-23-17	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-27 14:31:40.072496-04	26	EXECUTED	7:9705b6ced6d695eda2b8e1d33175b12b	addColumn, addForeignKeyConstraint	Add Country Counts should be related to the country list	\N	3.1.1
DGJ	MLDS-23-18	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-27 15:35:19.978072-04	27	EXECUTED	7:24f53cb7ae525c70c1f6d9faf12909bc	dropColumn	Practices are now just counts rather than type of commercial usage entry	\N	3.1.1
DGJ	MLDS-23-19	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-06-30 14:27:07.088047-04	28	EXECUTED	7:44d417d7ddc481736be4da832a7898c2	addColumn, update	Mark type of licensee	\N	3.1.1
DGJ	MLDS-23-20	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-02 10:18:42.351614-04	29	EXECUTED	7:b39935295c1ec02bdc2e73642eaa9765	addColumn	Generalize usage with context fields	\N	3.1.1
DGJ	MLDS-23-21	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-02 10:45:13.584147-04	30	EXECUTED	7:1bb8e28fbb5cecd04fd32e1ae2b5a183	renameColumn	Generalize usage with context fields	\N	3.1.1
DGJ	MLDS-23-22	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-02 11:49:53.250341-04	31	EXECUTED	7:d6867509a7794d85e2104b01c7784e91	addColumn, update	Include licensee type on usage report	\N	3.1.1
MLDS-04-4	AC	classpath:db-changelog.xml	2014-07-03 10:46:04.110257-04	32	EXECUTED	7:37da8419d3822ad1cd43e282c3dda207	renameColumn (x2), addColumn (x3), renameColumn (x2), addColumn (x3), renameColumn, addColumn (x4), dropColumn (x2)	Adding in the rest of fields	\N	3.1.1
MLDS-04-5	AC	classpath:db-changelog.xml	2014-07-04 11:13:13.967787-04	33	EXECUTED	7:8d24e230b08411b6cf511a331896986c	addColumn		\N	3.1.1
MLDS-20-1	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-04 11:13:14.135089-04	34	EXECUTED	7:b66f8abe1fb135dc4b5a77223d6d1424	createTable	Storage for password reset tokens	\N	3.1.1
MLDS-04-6	AC	classpath:db-changelog.xml	2014-07-04 17:28:25.591751-04	35	EXECUTED	7:bd18595ec5905604c30c20471b365735	addColumn (x2)		\N	3.1.1
MLDS-04-7	AC	classpath:db-changelog.xml	2014-07-04 17:28:25.650683-04	36	EXECUTED	7:225a749ffda7da06c4e8867742c80270	addColumn		\N	3.1.1
MLDS-234-1	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-07 09:29:33.607652-04	37	EXECUTED	7:0bd6dbb1a397bbd9f22c74550296b571	addColumn	Add flags to Country to identify Members that aren''t signed up to use MLDS	\N	3.1.1
MLDS-234-2	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-07 09:29:33.633144-04	38	EXECUTED	7:bdbb4bd9fc4d93f54554c8d53c90ee8f	sql (x2)	Put US and UK entries in for alternative registration locations	\N	3.1.1
MLDS-256-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-11 11:39:06.944001-04	39	EXECUTED	7:5afa0326621f5de8a5d84852f5a52f65	createTable, addPrimaryKey, addForeignKeyConstraint	Add Package	\N	3.1.1
MLDS-256-2	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-11 12:19:21.434079-04	40	EXECUTED	7:72f95c7b393d63e1e2f821faa2d296b0	renameTable, renameColumn	Rename Package to ReleasePackage	\N	3.1.1
MLDS-256-3	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-11 12:47:14.748993-04	41	EXECUTED	7:4ddd0343a93e3618f87510b9d01072b6	createTable, addPrimaryKey, addForeignKeyConstraint	Add ReleaseVersion as child of ReleasePackage	\N	3.1.1
MLDS-256-4	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-11 13:06:13.435982-04	42	EXECUTED	7:ad0135d5ff6c19f9df16311db311b138	createTable, addPrimaryKey	Add ReleaseFile as child of ReleaseVersion	\N	3.1.1
MLDS-256-5	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-11 16:11:56.377584-04	43	EXECUTED	7:e5527c90ba284f91417b79fd6e62cb35	modifyDataType	Fix JH type definition of timestamp column	\N	3.1.1
MLDS-256-6	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-14 17:03:30.095742-04	44	EXECUTED	7:19f3564a88a124293ec23b6000dc78c8	addColumn	Add online flag to ReleaseVersion	\N	3.1.1
MLDS-256-7	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-14 17:03:30.137672-04	45	EXECUTED	7:283fd3cbbf9aaed12d9791c5b77b4aaf	addColumn	Add inactive_at flag/timestamp to release	\N	3.1.1
MLDS-256-8	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-15 10:18:25.159863-04	46	EXECUTED	7:651e9642046fe3a91300ea4ba4991903	addColumn	Add inactive_at flag/timestamp to release	\N	3.1.1
MLDS-274-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-15 15:13:53.672584-04	47	EXECUTED	7:6285d5cebd3fe62fd59d7b4a40e93bac	addColumn	Add mgmt fields to application to support pending applications	\N	3.1.1
MLDS-274-2	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-15 16:51:22.541827-04	48	EXECUTED	7:508f3436401a785650fdfd310d9520c0	addColumn, sql (x3), dropColumn (x2)	Migrate to application approval state	\N	3.1.1
MLDS-274-3	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-18 10:16:36.392683-04	49	EXECUTED	7:a1553662f564b005e60b1836fa8bb1af	addColumn	Associate application with the usage at time of submission	\N	3.1.1
MLDS-304-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-18 16:13:35.544623-04	50	EXECUTED	7:1a87c801a2da1bdf27270ef4711e4755	addColumn	Add implementation status	\N	3.1.1
MLDS-274-4	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-21 12:21:36.700504-04	51	EXECUTED	7:0285318a16dc84d48c119744e2ef6a24	addNotNullConstraint	Stricter application approval state	\N	3.1.1
MLDS-259-1	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-23 10:26:17.8363-04	52	EXECUTED	7:072204dfa1004da4a71f8d400e4e5506	renameTable	Renaming Licensee table to Affiliate	\N	3.1.1
MLDS-259-2	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-23 10:26:17.870761-04	53	EXECUTED	7:19185b7b6184b25a4d2803f7167d336a	renameColumn (x2)	Renaming Licensee columns to Affiliate	\N	3.1.1
MLDS-259-3	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-23 12:01:31.877192-04	54	EXECUTED	7:64138b58f1b912696257d9b1a0635b19	addColumn (x5)	Add entity references audit log	\N	3.1.1
MLDS-259-4	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-23 14:00:22.184111-04	55	EXECUTED	7:7e863d6f72bc5b3961bac42f1675f418	dropForeignKeyConstraint (x5)	Remove entity foreign key constraints from audit log	\N	3.1.1
MLDS-372-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-23 16:52:12.64354-04	56	EXECUTED	7:00c0124f8e0049b1c6ea131db6fe988d	createTable, addPrimaryKey, addUniqueConstraint	Add Member	\N	3.1.1
MLDS-372-2	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-24 09:43:53.407488-04	57	EXECUTED	7:256e49a762f56f155ae8d92f3470b0da	sql (x2)	Load Member users	\N	3.1.1
MLDS-372-3	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.346525-04	58	EXECUTED	7:c48bd13ae881c9e8e6185b104737e040	sql (x3)	Create login users for the IHTSDO and demo SWEDEN members	\N	3.1.1
MLDS-259-3	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.448539-04	59	EXECUTED	7:173fcbf360a83084a4f8559ca7d5af07	createTable, addPrimaryKey	Extract details object from Application and share with Affiliate	\N	3.1.1
MLDS-259-4	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.482315-04	60	EXECUTED	7:1efa9b62eea7b4a5a63220da5deaba57	createTable, addForeignKeyConstraint	Extract details object from Application and share with Affiliate	\N	3.1.1
MLDS-259-5	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.548611-04	61	EXECUTED	7:0be70341cec20c7cb7c5dbc2e1796ee7	dropTable	removing table address table	\N	3.1.1
MLDS-259-6	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.642401-04	62	EXECUTED	7:fc5ac88f10a2cb43a38522116d114cfc	addColumn (x8)	removing table address table	\N	3.1.1
MLDS-259-7	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.665846-04	63	EXECUTED	7:a61a95610fc0080435f246bbf42a02ce	addColumn, addForeignKeyConstraint	adding in affiliate_details_id column	\N	3.1.1
MLDS-259-8	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.707832-04	64	EXECUTED	7:1c9016a5a37822f298db23d148fda396	addForeignKeyConstraint (x2)	adding foreign keys to countries in addresses	\N	3.1.1
MLDS-259-9	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.733084-04	65	EXECUTED	7:8d6b094870e211dd79abf8e601b98af4	addColumn, modifyDataType (x4)	adding missed column and updating affiliate address column types	\N	3.1.1
MLDS-259-10	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 10:27:30.757783-04	66	EXECUTED	7:12be09e7ff52ff5eadaad7c9308fdc57	dropColumn, addColumn	dropping affiliate organization column and adding it to affiliate details	\N	3.1.1
MLDS-372-4	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 17:00:41.218132-04	67	EXECUTED	7:49f0b0c008c10fa22931a30c57830d57	addColumn, sql, addNotNullConstraint	Add member to ReleasePackage	\N	3.1.1
MLDS-259-11	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-25 17:00:41.261934-04	68	EXECUTED	7:c8495b61608ed6ab78f2390ecd48c528	addColumn (x6)	adding missing columns to affiliate details and adding affiliate details to application	\N	3.1.1
MLDS-300-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-29 14:34:35.730707-04	69	EXECUTED	7:dc8b892dbd6c10d35d8f1205da9b73f0	addColumn, sql (x2)	Associate Country with Member	\N	3.1.1
MLDS-300-2	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-29 15:33:42.515504-04	70	EXECUTED	7:c7a7e70c8355da27ce395aab7f9e7187	addColumn (x2), sql (x2), addNotNullConstraint (x2)	Associate Member with Affiliate and Application	\N	3.1.1
MLDS-308-1	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-07-31 16:57:24.733264-04	71	EXECUTED	7:20efc5ddcfb4b115b4cd9cc9d2110f92	addColumn, addNotNullConstraint	Convert application to class heirarchy.  Add discriminator column.	\N	3.1.1
MLDS-308-2	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-01 09:14:34.574018-04	72	EXECUTED	7:54b69d12a185a7bc48538859c4d10b5c	addColumn	Add reason column for Extension application	\N	3.1.1
MLDS-308-3	MB/DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-01 11:35:01.884126-04	73	EXECUTED	7:9c339ff19963dac1cc11cbd0f0533985	addColumn, addForeignKeyConstraint, sql (x2), addNotNullConstraint		\N	3.1.1
MLDS-308-4	AC	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-05 10:53:15.216437-04	74	EXECUTED	7:9a48d945b84d20c30f84422f647ae3e1	dropNotNullConstraint	Removing not null contraint	\N	3.1.1
MLDS-325-1	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-11 09:54:39.007237-04	75	EXECUTED	7:2fd4e82e7531b8c79c1533f9ea05773e	dropNotNullConstraint (x2)	Imported affiliates may not have related user in this system	\N	3.1.1
MLDS-325-2	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-11 09:54:39.199425-04	76	EXECUTED	7:356c4f80228e7c34b3ddf7adb6979f8a	addColumn	Record import source key	\N	3.1.1
MLDS-325-3	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-11 10:11:42.764776-04	77	EXECUTED	7:5b335b9f8fc88cfe00c41696a48d7500	dropColumn, addColumn	Record import source key	\N	3.1.1
MLDS-325-4	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-12 14:56:45.050692-04	78	EXECUTED	7:8d88f991c00e769931cf0e7cb52752c4	sql	Add remaining members	\N	3.1.1
MLDS-325-5	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-12 15:08:04.344878-04	79	EXECUTED	7:e362ad042cfb8bcd41dbd8fa63c8e0cf	sql	Add matching members	\N	3.1.1
MLDS-325-6	DGJ	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-12 15:16:51.885762-04	80	EXECUTED	7:774d44bab793d4fe0d0d924ef57d7b22	sql	Re-enable sign-up for Sweden to support manual testing...	\N	3.1.1
MLDS-325-4	MB	classpath:config/liquibase/changelog/db-changelog-001.xml	2014-08-14 10:14:29.458327-04	81	EXECUTED	7:a0f2ae625d9c04be8c2f189ddb692cb1	addUniqueConstraint	Unique constraint on homeMember/importKey	\N	3.1.1
\.


--
-- Data for Name: databasechangeloglock; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY databasechangeloglock (id, locked, lockgranted, lockedby) FROM stdin;
1	f	\N	\N
\.


--
-- Data for Name: email_domain_blacklist; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY email_domain_blacklist (domain_id, domainname) FROM stdin;
2045	bad.com
2047	somekindofbad.com
2048	bad.com
2214	reallybad.com
\.


--
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY event (event_id, type, description, "timestamp", event_sub_type, principal, browser_type, browser_version, ip_address, locale, session_id, user_agent) FROM stdin;
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: mlds
--

SELECT pg_catalog.setval('hibernate_sequence', 2810, true);


--
-- Data for Name: member; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY member (member_id, key, created_at) FROM stdin;
1437	IHTSDO	2014-07-24 09:43:53.392987-04
1438	SE	2014-07-24 09:43:53.392987-04
2308	AF	2014-08-06 16:02:55.520693-04
2309	FR	2014-08-06 16:02:59.632654-04
2310	ZW	2014-08-06 16:03:22.664808-04
2607	AU	2014-08-12 15:08:04.313843-04
2608	BE	2014-08-12 15:08:04.313843-04
2609	BN	2014-08-12 15:08:04.313843-04
2610	CA	2014-08-12 15:08:04.313843-04
2611	CL	2014-08-12 15:08:04.313843-04
2612	CZ	2014-08-12 15:08:04.313843-04
2613	DK	2014-08-12 15:08:04.313843-04
2614	EE	2014-08-12 15:08:04.313843-04
2615	HK	2014-08-12 15:08:04.313843-04
2616	IS	2014-08-12 15:08:04.313843-04
2617	IN	2014-08-12 15:08:04.313843-04
2618	IL	2014-08-12 15:08:04.313843-04
2619	LT	2014-08-12 15:08:04.313843-04
2620	MY	2014-08-12 15:08:04.313843-04
2621	MT	2014-08-12 15:08:04.313843-04
2622	NL	2014-08-12 15:08:04.313843-04
2623	NZ	2014-08-12 15:08:04.313843-04
2624	PL	2014-08-12 15:08:04.313843-04
2625	PT	2014-08-12 15:08:04.313843-04
2626	SG	2014-08-12 15:08:04.313843-04
2627	SK	2014-08-12 15:08:04.313843-04
2628	SI	2014-08-12 15:08:04.313843-04
2629	ES	2014-08-12 15:08:04.313843-04
2630	GB	2014-08-12 15:08:04.313843-04
2631	US	2014-08-12 15:08:04.313843-04
2632	UY	2014-08-12 15:08:04.313843-04
\.


--
-- Data for Name: password_reset_token; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY password_reset_token (password_reset_token_id, user_login, created) FROM stdin;
dbf3d80b-f3aa-4655-b170-b677cc5ff81f	\N	2014-07-07 14:07:35.551-04
c2df43f2-0f2d-4ac8-a2b5-0d31f13c8cc0	able920@mailinator.com	2014-07-07 14:08:10.244-04
0b2342d1-f58d-40da-bc28-10b72a44b9eb	able920@mailinator.com	2014-07-07 14:24:45.088-04
f26f5183-d4de-45e9-85ef-29d61914a520	able942@mailinator.com	2014-08-05 13:56:53.42-04
1e59ab5c-ed23-4878-91e1-0ff045325cef	able944@mailinator.com	2014-08-05 16:17:58.577-04
8549a723-464a-4c78-a93a-11d5ce42fdec	able948@mailinator.com	2014-08-06 10:44:22.758-04
8d5d039b-66fc-4372-af71-8959a098f6fc	able948@mailinator.com	2014-08-06 10:44:22.758-04
13f8439c-0228-4a86-8dc5-33ae547f68ae	able947@mailinator.com	2014-08-06 12:37:46.318-04
7bb280b4-650e-4cc4-8260-aeea8ace0d86	able947@mailinator.com	2014-08-06 12:40:31.149-04
cdfe3da3-a03d-41c0-8e66-c4428686d5d2	able947@mailinator.com	2014-08-06 12:48:14.367-04
3c2afe51-838d-4f32-92fd-9ac1c72a6071	able950@mailinator.com	2014-08-12 15:18:55.801-04
\.


--
-- Data for Name: release_file; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY release_file (release_file_id, release_version_id, created_at, label, download_url) FROM stdin;
1140	1138	2014-07-17 12:58:37.399-04	Hello	http://google.com
1398	1396	2014-07-23 12:44:32.192-04	my file 1	http://myfile1.com
1400	1396	2014-07-23 12:45:32.213-04	my file 2	http://myfile2.com
1406	1404	2014-07-23 12:54:01.761-04	my file 2.1	http://myfile2.1.com
1417	1410	2014-07-23 16:28:08.261-04	my file 3.11	http://myfile3.11
1895	1893	2014-08-01 10:07:24.866-04	a	http://a
1897	1893	2014-08-01 10:07:32.129-04	b	http://b
1901	1899	2014-08-01 10:07:56.003-04	c	http://c
1903	1899	2014-08-01 10:08:01.954-04	d	http://d
1913	1911	2014-08-01 10:09:12.037-04	a	http://a
1920	1918	2014-08-01 10:09:58.548-04	a	http://a
2324	2322	2014-08-06 16:19:53.607-04	1	http://1
2326	2322	2014-08-06 16:20:00.182-04	2	http://2
2331	2329	2014-08-06 16:20:23.382-04	3	http://3
2342	2340	2014-08-06 16:21:02.246-04	1	http://1
2344	2340	2014-08-06 16:21:08.983-04	2	http://2
\.


--
-- Data for Name: release_package; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY release_package (release_package_id, created_by, created_at, name, description, inactive_at, member_id) FROM stdin;
1047	\N	2014-07-11 15:02:21.173-04	aoeu111	aoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh naoeu nouhtnoehtnho etnuh n	2014-07-15 11:36:09.615721-04	1437
1394	admin	2014-07-23 12:41:08.419-04	My package	my package descr	\N	1437
1048	\N	2014-07-11 15:02:27.501-04	aoeu	aoeu	\N	1437
1049	\N	2014-07-11 15:04:26.189-04	aoeu	aoeu	\N	1437
1050	\N	2014-07-11 15:05:22.787-04	',.p	',.p	\N	1437
1052	admin	2014-07-11 15:19:07.173-04	qjk	qjkx	\N	1437
1063	admin	2014-07-11 16:58:09.645-04	Test Package	Our description	\N	1437
1046	\N	2014-07-11 15:01:31.785-04	aoeua	aoeu	\N	1437
1073	admin	2014-07-15 10:27:57.011-04	new 1	ou	\N	1437
1067	admin	2014-07-14 13:33:08.017-04	more11	more 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tnmore 2 no hutnoh utnh otne tnoh tnuh tn	2014-07-15 10:52:22.362752-04	1437
1909	sweden	2014-08-01 10:08:52.49-04	Sweden A	a	\N	1438
1916	sweden	2014-08-01 10:09:42.362-04	Sweden Offline	offline	\N	1438
1891	sweden	2014-08-01 10:06:55.479-04	Sweden Z1	swdene	\N	1438
1045	\N	2014-07-11 15:01:18.304-04	aoeu112aaab19333	some kind of description	\N	1437
2320	af	2014-08-06 16:19:34.451-04	Afgain A	afgan a	\N	2308
2338	fr	2014-08-06 16:20:46.86-04	France A	france 1	\N	2309
\.


--
-- Data for Name: release_version; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY release_version (release_version_id, release_package_id, created_by, created_at, name, description, published_at, online, inactive_at) FROM stdin;
1138	1045	admin	2014-07-17 12:58:20.599-04	version 1	new version	2014-07-17 12:58:48.57-04	t	\N
1370	1045	admin	2014-07-22 13:58:46.959-04	Version 2	version 2 description	2014-07-22 13:58:57.712-04	t	\N
1396	1394	admin	2014-07-23 12:41:28.842-04	my version 1	my version 1 desc2	2014-07-23 12:45:37.313-04	f	\N
1404	1394	admin	2014-07-23 12:53:32.092-04	my version 2	my version 2 decs	2014-07-23 12:54:13.196-04	f	\N
1410	1394	admin	2014-07-23 12:55:21.176-04	my version 3aa	ou	2014-07-23 16:29:54.316-04	f	\N
1893	1891	sweden	2014-08-01 10:07:15.482-04	Vers 1	1	2014-08-01 10:08:13.478-04	t	\N
1899	1891	sweden	2014-08-01 10:07:43.913-04	Ver 2	2	2014-08-01 10:08:29.781-04	t	\N
1911	1909	sweden	2014-08-01 10:09:04.415-04	VerA 1	a	2014-08-01 10:09:21.283-04	t	\N
1918	1916	sweden	2014-08-01 10:09:52.205-04	Ver 1 OFF	1	\N	f	\N
2322	2320	af	2014-08-06 16:19:43.495-04	AF Version 1	af 1	2014-08-06 16:20:07.181-04	t	\N
2329	2320	af	2014-08-06 16:20:15.671-04	AF Version 2	af 2	\N	f	\N
2340	2338	fr	2014-08-06 16:20:55.922-04	FR v1	fr 1	2014-08-06 16:21:12.805-04	t	\N
\.


--
-- Data for Name: t_authority; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_authority (name) FROM stdin;
ROLE_ADMIN
ROLE_USER
ROLE_STAFF
ROLE_STAFF_SE
ROLE_STAFF_IHTSDO
ROLE_STAFF_ZW
ROLE_STAFF_AF
ROLE_STAFF_FR
\.


--
-- Data for Name: t_persistent_audit_event; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_persistent_audit_event (event_id, principal, event_date, event_type, application_id, affiliate_id, release_package_id, release_version_id, release_file_id) FROM stdin;
1	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
3	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
4	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
5	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
6	admin	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
7	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
8	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
9	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
11	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
12	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
13	admin	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
14	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
15	anonymousUser	2014-06-24 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
16	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
17	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
18	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
19	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
22	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
27	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
36	able916@mailinator.com	2014-06-24 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
63	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
67	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
69	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
71	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
72	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
73	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
74	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
78	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
83	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
84	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
85	admin	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
86	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
87	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
88	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
89	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
90	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
91	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
92	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
93	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
94	able916@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
95	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
96	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
97	admin	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
98	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
99	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
100	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
101	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
102	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
103	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
104	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
105	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
106	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
107	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
108	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
109	able917@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
112	admin	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
113	able917@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
114	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
115	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
116	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
117	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
120	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
121	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
122	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
123	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
124	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
131	able918@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
132	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
133	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
134	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
135	anonymousUser	2014-06-26 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
136	able919@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
138	admin	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
143	able919@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
146	able919@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
147	able919@mailinator.com	2014-06-26 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
149	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
150	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
151	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
152	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
157	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
168	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
169	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
171	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
172	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
173	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
174	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
175	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
177	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
187	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
191	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
194	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
195	able919@mailinator.com	2014-06-27 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
196	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
197	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
198	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
200	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
204	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
208	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
209	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
210	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
211	able919@mailinator.com	2014-06-30 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
215	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
216	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
217	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
218	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
219	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
224	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
248	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
251	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
256	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
260	anonymousUser	2014-07-02 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
261	anonymousUser	2014-07-02 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
262	able916@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
263	anonymousUser	2014-07-02 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
264	anonymousUser	2014-07-02 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
265	able919@mailinator.com	2014-07-02 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
270	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
273	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
277	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
286	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
287	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
292	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
293	able919@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
294	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
295	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
296	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
297	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
298	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
299	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
300	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
301	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
302	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
303	anonymousUser	2014-07-03 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
304	able920@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
309	able920@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
339	able920@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
350	able920@mailinator.com	2014-07-03 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
648	able920@mailinator.com	2014-07-04 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
650	anonymousUser	2014-07-04 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
651	anonymousUser	2014-07-04 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
652	able920@mailinator.com	2014-07-04 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
674	able920@mailinator.com	2014-07-04 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
686	admin	2014-07-04 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
687	able920@mailinator.com	2014-07-04 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
690	able920@mailinator.com	2014-07-07 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
691	able920@mailinator.com	2014-07-07 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
692	able920@mailinator.com	2014-07-07 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
702	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
706	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
707	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
709	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
710	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
711	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
712	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
713	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
714	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
715	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
716	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
717	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
718	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
719	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
720	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
721	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
722	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
723	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
724	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
725	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
726	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
727	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
728	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
729	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
730	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
731	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
735	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
736	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
737	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
738	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
739	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
740	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
741	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
742	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
743	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
744	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
745	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
746	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
747	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
748	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
749	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
750	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
751	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
752	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
753	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
754	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
755	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
756	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
757	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
758	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
759	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
760	anonymousUser	2014-07-07 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
761	able920@mailinator.com	2014-07-07 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
762	able920@mailinator.com	2014-07-07 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
763	able920@mailinator.com	2014-07-08 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1002	anonymousUser	2014-07-09 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1003	anonymousUser	2014-07-09 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1004	able920@mailinator.com	2014-07-09 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1017	admin	2014-07-09 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1022	anonymousUser	2014-07-09 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1023	anonymousUser	2014-07-09 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1024	anonymousUser	2014-07-09 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1027	able920@mailinator.com	2014-07-10 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1028	able920@mailinator.com	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1029	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1030	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1031	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1032	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1033	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1034	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1035	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1036	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1037	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1038	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1039	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1040	able920@mailinator.com	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1041	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1042	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1043	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1044	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1051	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1053	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1054	anonymousUser	2014-07-11 00:00:00-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1055	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1056	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1057	admin	2014-07-11 00:00:00-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1058	admin	2014-07-11 16:12:03.997-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1059	anonymousUser	2014-07-11 16:12:41.478-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1060	anonymousUser	2014-07-11 16:12:42.662-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1061	admin	2014-07-11 16:12:45.937-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1062	admin	2014-07-11 16:57:22.607-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1064	admin	2014-07-11 16:58:09.679-04	RELEASE_PACKAGE_CREATED	\N	\N	\N	\N	\N
1065	admin	2014-07-14 11:03:25.357-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1066	admin	2014-07-14 11:43:39.814-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1068	admin	2014-07-14 13:33:08.051-04	RELEASE_PACKAGE_CREATED	\N	\N	\N	\N	\N
1069	admin	2014-07-15 10:07:25.749-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1070	admin	2014-07-15 10:18:29.521-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1071	admin	2014-07-15 10:20:57.626-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1072	admin	2014-07-15 10:21:38.022-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1074	admin	2014-07-15 10:27:57.049-04	RELEASE_PACKAGE_CREATED	\N	\N	\N	\N	\N
1075	admin	2014-07-15 10:49:42.807-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1076	admin	2014-07-15 10:58:26.663-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1077	admin	2014-07-15 11:36:02.111-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1078	admin	2014-07-15 11:36:09.581-04	RELEASE_PACKAGE_DELETED	\N	\N	\N	\N	\N
1079	admin	2014-07-15 12:57:08.749-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1080	admin	2014-07-15 13:00:10.426-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1081	admin	2014-07-15 13:01:41.332-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1082	admin	2014-07-15 13:06:47.057-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1083	admin	2014-07-15 13:11:43.181-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1084	anonymousUser	2014-07-15 13:14:37.34-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1085	anonymousUser	2014-07-15 13:14:37.48-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1086	anonymousUser	2014-07-15 13:14:50.543-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1087	anonymousUser	2014-07-15 13:14:52.231-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1088	anonymousUser	2014-07-15 13:14:54.308-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1089	admin	2014-07-15 13:14:58.526-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1090	admin	2014-07-15 13:25:04.383-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1091	anonymousUser	2014-07-15 13:37:06.248-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1092	anonymousUser	2014-07-15 13:37:12.448-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1093	able923@mailinator.com	2014-07-15 13:38:05.42-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1100	admin	2014-07-15 15:14:01.116-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1101	able923@mailinator.com	2014-07-15 15:15:10.865-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1102	admin	2014-07-15 16:51:49.032-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1103	able923@mailinator.com	2014-07-15 17:02:52.795-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1104	able923@mailinator.com	2014-07-15 17:02:52.802-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1105	able923@mailinator.com	2014-07-16 09:26:00.056-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1106	admin	2014-07-16 09:26:27.278-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1107	able923@mailinator.com	2014-07-16 09:44:34.642-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1108	admin	2014-07-16 09:44:44.817-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1109	admin	2014-07-16 10:36:24.9-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1110	admin	2014-07-16 10:37:56.112-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1111	able923@mailinator.com	2014-07-16 10:59:16.985-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1112	admin	2014-07-16 14:15:44.063-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1113	able923@mailinator.com	2014-07-16 14:37:14.708-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1114	able923@mailinator.com	2014-07-16 14:37:14.715-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1119	admin	2014-07-16 15:12:18.205-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1120	admin	2014-07-16 15:14:31.922-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1121	anonymousUser	2014-07-16 15:15:11.662-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1122	anonymousUser	2014-07-16 15:15:11.709-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1123	anonymousUser	2014-07-16 15:15:11.742-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1124	anonymousUser	2014-07-16 15:16:05.521-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1125	anonymousUser	2014-07-16 15:16:12.495-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1126	able920@mailinator.com	2014-07-16 15:17:18.889-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1127	anonymousUser	2014-07-16 15:18:39.416-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1128	anonymousUser	2014-07-16 15:18:39.439-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1129	anonymousUser	2014-07-16 15:18:40.179-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1130	able923@mailinator.com	2014-07-16 15:18:58.884-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1131	admin	2014-07-16 17:01:02.885-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1132	admin	2014-07-16 17:04:27.024-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1133	able923@mailinator.com	2014-07-16 17:22:21.699-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1134	admin	2014-07-17 12:46:25.977-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1135	able923@mailinator.com	2014-07-17 12:46:30.718-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1136	able923@mailinator.com	2014-07-17 12:46:30.724-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1137	able923@mailinator.com	2014-07-17 12:50:59.113-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1139	admin	2014-07-17 12:58:20.619-04	RELEASE_VERSION_CREATED	\N	\N	\N	\N	\N
1141	admin	2014-07-17 12:58:37.405-04	RELEASE_FILE_CREATED	\N	\N	\N	\N	\N
1142	admin	2014-07-17 13:02:51.616-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1143	admin	2014-07-17 13:02:51.694-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1144	able923@mailinator.com	2014-07-17 13:03:47.884-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1145	able923@mailinator.com	2014-07-17 13:04:39.84-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1146	admin	2014-07-17 13:06:32.205-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1147	able923@mailinator.com	2014-07-17 13:26:17.17-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1148	admin	2014-07-17 13:26:44.642-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1149	able923@mailinator.com	2014-07-17 13:32:44.687-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1150	admin	2014-07-17 13:59:07.457-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1151	able923@mailinator.com	2014-07-17 13:59:30.596-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1152	admin	2014-07-17 13:59:43.86-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1153	able923@mailinator.com	2014-07-17 14:00:49.219-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1154	able923@mailinator.com	2014-07-17 14:09:18.396-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1155	able923@mailinator.com	2014-07-17 14:09:20.362-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1156	anonymousUser	2014-07-17 14:10:33.473-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1157	anonymousUser	2014-07-17 14:10:40.599-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1158	anonymousUser	2014-07-17 14:10:42.607-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1159	able923@mailinator.com	2014-07-17 14:11:15.223-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1160	admin	2014-07-17 16:57:55.867-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1161	admin	2014-07-18 10:16:45.452-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1162	anonymousUser	2014-07-18 13:13:00.209-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1166	anonymousUser	2014-07-18 13:13:25.924-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1167	anonymousUser	2014-07-18 13:13:38.47-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1172	able925@mailinator.com	2014-07-18 13:16:46.596-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1173	admin	2014-07-18 13:16:55.085-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1174	admin	2014-07-18 13:29:58.374-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1175	admin	2014-07-18 13:34:12.919-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1176	anonymousUser	2014-07-18 13:40:07.063-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1177	anonymousUser	2014-07-18 13:40:07.246-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1178	anonymousUser	2014-07-18 13:40:07.325-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1179	able925@mailinator.com	2014-07-18 13:40:44.446-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1180	able925@mailinator.com	2014-07-18 13:43:51.432-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1181	admin	2014-07-18 13:44:51.525-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1182	able925@mailinator.com	2014-07-18 13:56:58.78-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1183	admin	2014-07-18 13:58:43.433-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1184	admin	2014-07-18 14:27:11.778-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1185	admin	2014-07-18 14:31:43.585-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1186	admin	2014-07-18 14:34:15.179-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1187	admin	2014-07-18 14:37:14.626-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1188	admin	2014-07-18 14:37:38.351-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1189	able925@mailinator.com	2014-07-18 14:37:58.153-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1190	able925@mailinator.com	2014-07-18 14:44:46.373-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1191	able925@mailinator.com	2014-07-18 14:45:57.488-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1192	admin	2014-07-18 14:46:05.407-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1193	admin	2014-07-18 14:46:29.377-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1194	able925@mailinator.com	2014-07-18 14:50:20.388-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1195	able925@mailinator.com	2014-07-18 14:57:20.617-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1196	able925@mailinator.com	2014-07-18 15:02:08.032-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1197	admin	2014-07-18 15:07:01.471-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1198	able925@mailinator.com	2014-07-18 15:07:38.209-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1199	anonymousUser	2014-07-18 15:40:23.19-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1203	anonymousUser	2014-07-18 15:40:44.449-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1204	anonymousUser	2014-07-18 15:41:01.054-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1206	able926@mailinator.com	2014-07-18 15:49:18.854-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1207	anonymousUser	2014-07-18 15:51:13.324-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1211	anonymousUser	2014-07-18 15:51:36.456-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1212	anonymousUser	2014-07-18 15:51:51.745-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1213	anonymousUser	2014-07-18 15:52:00.707-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1214	anonymousUser	2014-07-18 15:52:00.775-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1215	anonymousUser	2014-07-18 15:52:01.793-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1216	able926@mailinator.com	2014-07-18 15:52:12.816-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1217	admin	2014-07-18 15:52:28.27-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1218	able926@mailinator.com	2014-07-18 16:19:43.208-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1219	able926@mailinator.com	2014-07-18 16:22:00.416-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1220	anonymousUser	2014-07-18 16:26:24.8-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1221	anonymousUser	2014-07-18 16:26:25.199-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1222	anonymousUser	2014-07-18 16:26:25.472-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1223	anonymousUser	2014-07-18 16:26:25.474-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1224	able926@mailinator.com	2014-07-18 16:26:41.96-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1225	able926@mailinator.com	2014-07-18 16:27:02.655-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1226	admin	2014-07-18 16:28:23.775-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1227	admin	2014-07-18 16:30:23.329-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1228	admin	2014-07-21 11:19:52.785-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1229	anonymousUser	2014-07-21 11:26:12.549-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1230	admin	2014-07-21 11:26:19.09-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1231	anonymousUser	2014-07-21 11:27:54.19-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1232	anonymousUser	2014-07-21 11:27:56.516-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1233	anonymousUser	2014-07-21 11:28:09.16-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1234	admin	2014-07-21 11:28:12.267-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1235	admin	2014-07-21 11:28:32.915-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1236	anonymousUser	2014-07-21 11:33:11.081-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1237	anonymousUser	2014-07-21 11:33:12.918-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1238	admin	2014-07-21 11:33:17.303-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1239	anonymousUser	2014-07-21 11:34:00.055-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1240	anonymousUser	2014-07-21 11:34:00.941-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1241	admin	2014-07-21 11:34:06.921-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1242	able926@mailinator.com	2014-07-21 11:34:13.731-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1243	anonymousUser	2014-07-21 11:34:17.444-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1244	anonymousUser	2014-07-21 11:34:20.039-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1245	able926@mailinator.com	2014-07-21 11:34:32.884-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1246	anonymousUser	2014-07-21 11:42:25.955-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1247	anonymousUser	2014-07-21 11:42:27.799-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1248	anonymousUser	2014-07-21 11:42:33.296-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1249	anonymousUser	2014-07-21 11:44:12.983-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1250	anonymousUser	2014-07-21 11:44:21.32-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1251	anonymousUser	2014-07-21 11:44:22.483-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1252	anonymousUser	2014-07-21 11:44:44.308-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1253	anonymousUser	2014-07-21 11:45:25.072-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1257	anonymousUser	2014-07-21 11:45:47.133-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1258	anonymousUser	2014-07-21 11:46:04.717-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1259	anonymousUser	2014-07-21 11:48:54.635-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1260	anonymousUser	2014-07-21 11:48:56.833-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1261	able928@mailinator.com	2014-07-21 11:49:10.186-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1262	anonymousUser	2014-07-21 11:52:56.931-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1266	anonymousUser	2014-07-21 11:53:24.411-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1267	anonymousUser	2014-07-21 11:53:39.554-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1268	anonymousUser	2014-07-21 12:09:53.892-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1272	anonymousUser	2014-07-21 12:10:21.264-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1273	anonymousUser	2014-07-21 12:10:41.135-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1274	anonymousUser	2014-07-21 12:21:52.179-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1275	anonymousUser	2014-07-21 12:21:52.31-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1280	anonymousUser	2014-07-21 12:24:24.574-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1281	anonymousUser	2014-07-21 12:24:48.571-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1282	anonymousUser	2014-07-21 12:34:06.834-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1283	anonymousUser	2014-07-21 12:34:06.886-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1284	anonymousUser	2014-07-21 12:34:22.09-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1285	able931@mailinator.com	2014-07-21 12:34:35.664-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1286	anonymousUser	2014-07-21 12:35:27.363-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1287	anonymousUser	2014-07-21 12:35:28.476-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1288	admin	2014-07-21 12:35:32.84-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1289	anonymousUser	2014-07-21 12:35:34.832-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1290	anonymousUser	2014-07-21 12:35:36.001-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1291	able926@mailinator.com	2014-07-21 12:35:48.388-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1294	able931@mailinator.com	2014-07-21 12:38:17.617-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1295	anonymousUser	2014-07-21 12:38:54.033-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1296	anonymousUser	2014-07-21 12:38:54.965-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1297	admin	2014-07-21 12:38:59.223-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1298	admin	2014-07-21 13:53:27.451-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1299	anonymousUser	2014-07-21 13:53:43.985-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1300	anonymousUser	2014-07-21 13:53:44.251-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1301	anonymousUser	2014-07-21 13:53:44.44-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1302	able931@mailinator.com	2014-07-21 13:53:55.394-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1303	anonymousUser	2014-07-21 16:22:16.59-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1307	anonymousUser	2014-07-21 16:22:38.275-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1308	anonymousUser	2014-07-21 16:22:51.111-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1309	anonymousUser	2014-07-21 16:31:32.381-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1310	anonymousUser	2014-07-21 16:31:32.724-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1311	anonymousUser	2014-07-21 16:31:32.948-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1312	able931@mailinator.com	2014-07-21 16:32:51.408-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1313	anonymousUser	2014-07-21 16:33:03.495-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1314	anonymousUser	2014-07-21 16:33:05.163-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1315	admin	2014-07-21 16:33:09.288-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1316	admin	2014-07-21 16:33:29.325-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1317	anonymousUser	2014-07-21 16:33:32.338-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1318	anonymousUser	2014-07-21 16:33:33.706-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1319	able931@mailinator.com	2014-07-21 16:33:43.884-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1320	able931@mailinator.com	2014-07-21 16:37:50.238-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1321	able931@mailinator.com	2014-07-21 16:42:53.733-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1322	able931@mailinator.com	2014-07-21 16:55:37.437-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1325	able931@mailinator.com	2014-07-21 16:59:01.668-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1326	able931@mailinator.com	2014-07-21 16:59:34.382-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1327	anonymousUser	2014-07-21 17:00:15.592-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1328	admin	2014-07-21 17:00:20.197-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1329	admin	2014-07-21 17:00:49.018-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1330	anonymousUser	2014-07-21 17:00:53.288-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1331	able931@mailinator.com	2014-07-21 17:01:06.499-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1332	able931@mailinator.com	2014-07-21 17:01:19.073-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1333	anonymousUser	2014-07-21 17:01:23.205-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1334	anonymousUser	2014-07-21 17:01:24.658-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1335	admin	2014-07-21 17:01:28.605-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1336	anonymousUser	2014-07-21 17:01:36.645-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1340	anonymousUser	2014-07-21 17:02:11.911-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1341	anonymousUser	2014-07-21 17:02:27.68-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1343	able932@mailinator.com	2014-07-21 17:03:49.78-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1344	anonymousUser	2014-07-21 17:04:03.607-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1345	anonymousUser	2014-07-21 17:04:04.883-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1346	admin	2014-07-21 17:04:08.919-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1347	admin	2014-07-21 17:04:21.418-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1348	admin	2014-07-21 17:04:43.315-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1349	anonymousUser	2014-07-21 17:04:47.692-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1350	anonymousUser	2014-07-21 17:04:49.619-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1351	able932@mailinator.com	2014-07-21 17:05:00.537-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1354	able932@mailinator.com	2014-07-21 17:05:35.25-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1356	anonymousUser	2014-07-21 17:05:44.964-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1359	anonymousUser	2014-07-21 17:06:17.533-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1360	anonymousUser	2014-07-21 17:06:18.78-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1361	able932@mailinator.com	2014-07-21 17:06:27.468-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1355	anonymousUser	2014-07-21 17:05:44.391-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1357	admin	2014-07-21 17:05:48.792-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1358	admin	2014-07-21 17:06:12.886-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1362	able932@mailinator.com	2014-07-21 17:08:31.327-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1363	able932@mailinator.com	2014-07-21 17:11:58.854-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1364	able932@mailinator.com	2014-07-21 17:12:27.311-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1365	able932@mailinator.com	2014-07-21 17:13:12.374-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1366	able932@mailinator.com	2014-07-21 17:27:21.736-04	APPLICATION_APPROVAL_STATE_CHANGED	\N	\N	\N	\N	\N
1367	anonymousUser	2014-07-21 17:27:29.943-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1368	anonymousUser	2014-07-21 17:27:31.135-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1369	admin	2014-07-21 17:27:35.585-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1371	admin	2014-07-22 13:58:46.979-04	RELEASE_VERSION_CREATED	\N	\N	\N	\N	\N
1372	anonymousUser	2014-07-22 14:09:23.042-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1373	anonymousUser	2014-07-22 14:09:24.017-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1374	able923@mailinator.com	2014-07-22 14:09:47.76-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1375	able923@mailinator.com	2014-07-23 10:27:04.785-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1376	able923@mailinator.com	2014-07-23 12:18:19.332-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1377	able923@mailinator.com	2014-07-23 12:18:34.199-04	APPLICATION_APPROVAL_STATE_CHANGED	1018	\N	\N	\N	\N
1378	anonymousUser	2014-07-23 12:19:20.821-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1379	anonymousUser	2014-07-23 12:19:23.063-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1380	admin	2014-07-23 12:19:27.604-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1381	admin	2014-07-23 12:19:38.515-04	APPLICATION_APPROVAL_STATE_CHANGED	1018	\N	\N	\N	\N
1382	anonymousUser	2014-07-23 12:28:12.477-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1386	anonymousUser	2014-07-23 12:28:34.41-04	AFFILIATE_CREATED	\N	1384	\N	\N	\N
1387	anonymousUser	2014-07-23 12:28:34.632-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1388	anonymousUser	2014-07-23 12:29:04.247-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1389	able933@mailinator.com	2014-07-23 12:30:03.928-04	APPLICATION_APPROVAL_STATE_CHANGED	1383	\N	\N	\N	\N
1390	anonymousUser	2014-07-23 12:30:07.833-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1391	anonymousUser	2014-07-23 12:30:09.996-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1392	admin	2014-07-23 12:30:14.086-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1393	admin	2014-07-23 12:40:31.315-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1395	admin	2014-07-23 12:41:08.456-04	RELEASE_PACKAGE_CREATED	\N	\N	1394	\N	\N
1397	admin	2014-07-23 12:41:28.86-04	RELEASE_VERSION_CREATED	\N	\N	1394	1396	\N
1399	admin	2014-07-23 12:44:32.197-04	RELEASE_FILE_CREATED	\N	\N	1394	1396	1398
1401	admin	2014-07-23 12:45:32.217-04	RELEASE_FILE_CREATED	\N	\N	1394	1396	1400
1402	admin	2014-07-23 12:52:39.864-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1403	admin	2014-07-23 12:52:40.01-04	RELEASE_VERSION_TAKEN_OFFLINE	\N	\N	1394	1396	\N
1405	admin	2014-07-23 12:53:32.101-04	RELEASE_VERSION_CREATED	\N	\N	1394	1404	\N
1407	admin	2014-07-23 12:54:01.768-04	RELEASE_FILE_CREATED	\N	\N	1394	1404	1406
1408	admin	2014-07-23 12:54:13.197-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1394	1404	\N
1409	admin	2014-07-23 12:54:36.652-04	RELEASE_VERSION_TAKEN_OFFLINE	\N	\N	1394	1404	\N
1411	admin	2014-07-23 12:55:21.18-04	RELEASE_VERSION_CREATED	\N	\N	1394	1410	\N
1413	admin	2014-07-23 12:55:41.395-04	RELEASE_FILE_CREATED	\N	\N	1394	1410	1412
1414	admin	2014-07-23 14:00:34.335-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1415	admin	2014-07-23 14:00:42.721-04	RELEASE_FILE_DELETED	\N	\N	1394	1410	1412
1416	admin	2014-07-23 16:27:45.401-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1418	admin	2014-07-23 16:28:08.286-04	RELEASE_FILE_CREATED	\N	\N	1394	1410	1417
1420	admin	2014-07-23 16:28:35.548-04	RELEASE_FILE_CREATED	\N	\N	1394	1410	1419
1421	admin	2014-07-23 16:28:37.999-04	RELEASE_FILE_DELETED	\N	\N	1394	1410	1419
1422	admin	2014-07-23 16:29:32.65-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1394	1410	\N
1423	admin	2014-07-23 16:30:02.231-04	RELEASE_VERSION_TAKEN_OFFLINE	\N	\N	1394	1410	\N
1424	anonymousUser	2014-07-23 16:30:21.35-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1428	anonymousUser	2014-07-23 16:30:44.513-04	AFFILIATE_CREATED	\N	1426	\N	\N	\N
1429	anonymousUser	2014-07-23 16:30:44.723-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1430	anonymousUser	2014-07-23 16:30:56.864-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1431	able934@mailinator.com	2014-07-23 16:31:30.461-04	APPLICATION_APPROVAL_STATE_CHANGED	1425	\N	\N	\N	\N
1432	anonymousUser	2014-07-23 16:31:33.977-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1433	anonymousUser	2014-07-23 16:31:35.398-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1434	admin	2014-07-23 16:31:38.71-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1435	admin	2014-07-23 16:31:58.999-04	APPLICATION_APPROVAL_STATE_CHANGED	1425	\N	\N	\N	\N
1436	admin	2014-07-23 16:52:18.147-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1439	admin	2014-07-25 10:38:04.594-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1443	anonymousUser	2014-07-25 10:39:07.102-04	AFFILIATE_CREATED	\N	1441	\N	\N	\N
1448	able935@mailinator.com	2014-07-25 10:40:28.197-04	APPLICATION_APPROVAL_STATE_CHANGED	1440	\N	\N	\N	\N
1449	anonymousUser	2014-07-25 10:50:03.887-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1450	anonymousUser	2014-07-25 10:50:03.929-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1451	anonymousUser	2014-07-25 10:50:03.931-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1452	able935@mailinator.com	2014-07-25 10:50:18.545-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1453	anonymousUser	2014-07-25 10:55:48.189-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1454	anonymousUser	2014-07-25 10:55:48.694-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1455	admin	2014-07-25 10:55:51.79-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1456	admin	2014-07-25 10:56:32.845-04	APPLICATION_APPROVAL_STATE_CHANGED	1440	\N	\N	\N	\N
1457	anonymousUser	2014-07-25 10:56:40.025-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1458	anonymousUser	2014-07-25 10:56:41.56-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1459	able935@mailinator.com	2014-07-25 10:56:54.176-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1460	anonymousUser	2014-07-25 11:14:59.852-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1461	anonymousUser	2014-07-25 11:15:01.442-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1462	admin	2014-07-25 11:15:04.425-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1463	anonymousUser	2014-07-25 11:15:11.423-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1464	anonymousUser	2014-07-25 11:15:12.491-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1465	able935@mailinator.com	2014-07-25 11:15:20.914-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1466	able935@mailinator.com	2014-07-25 11:16:10.342-04	APPLICATION_APPROVAL_STATE_CHANGED	1440	\N	\N	\N	\N
1467	anonymousUser	2014-07-25 11:16:13.248-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1468	anonymousUser	2014-07-25 11:16:14.314-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1469	admin	2014-07-25 11:16:17.732-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1470	admin	2014-07-25 11:16:32.786-04	APPLICATION_APPROVAL_STATE_CHANGED	1440	\N	\N	\N	\N
1471	anonymousUser	2014-07-25 11:16:35.932-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1472	anonymousUser	2014-07-25 11:16:37.913-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1473	able935@mailinator.com	2014-07-25 11:16:50.628-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1474	able935@mailinator.com	2014-07-25 12:20:58.032-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1475	anonymousUser	2014-07-25 14:47:43.435-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1476	anonymousUser	2014-07-25 14:47:48.55-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1477	admin	2014-07-25 14:47:52.095-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1478	anonymousUser	2014-07-25 14:48:20.096-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1479	anonymousUser	2014-07-25 14:48:21.353-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1480	able935@mailinator.com	2014-07-25 14:48:58.77-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1481	<unknown>	2014-07-25 15:31:39.218-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
1482	anonymousUser	2014-07-25 15:31:40.491-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1483	admin	2014-07-25 15:31:43.934-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1484	anonymousUser	2014-07-25 15:37:37.593-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1485	anonymousUser	2014-07-25 15:37:39.312-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1486	able935@mailinator.com	2014-07-25 15:38:09.642-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1487	able935@mailinator.com	2014-07-28 09:30:34.926-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1488	able935@mailinator.com	2014-07-28 10:02:25.057-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1489	able935@mailinator.com	2014-07-28 10:09:16.528-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1490	able935@mailinator.com	2014-07-28 11:10:13.926-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1491	able935@mailinator.com	2014-07-28 11:25:15.583-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1492	anonymousUser	2014-07-28 11:26:02.052-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1497	anonymousUser	2014-07-28 11:26:30.031-04	AFFILIATE_CREATED	\N	1495	\N	\N	\N
1498	anonymousUser	2014-07-28 11:26:30.246-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1499	anonymousUser	2014-07-28 11:26:48.95-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1503	able936@mailinator.com	2014-07-28 11:29:33.015-04	APPLICATION_APPROVAL_STATE_CHANGED	1494	\N	\N	\N	\N
1504	anonymousUser	2014-07-28 11:31:10.697-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1505	anonymousUser	2014-07-28 11:31:12.159-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1506	admin	2014-07-28 11:31:15.227-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1507	admin	2014-07-28 11:32:20.212-04	APPLICATION_APPROVAL_STATE_CHANGED	1494	\N	\N	\N	\N
1508	anonymousUser	2014-07-28 11:32:25.825-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1509	anonymousUser	2014-07-28 11:32:27.026-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1510	able936@mailinator.com	2014-07-28 11:32:38.325-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1511	able936@mailinator.com	2014-07-28 16:21:06.164-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1516	anonymousUser	2014-07-28 16:21:45.818-04	AFFILIATE_CREATED	\N	1514	\N	\N	\N
1517	anonymousUser	2014-07-28 16:22:14.64-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1518	able937@mailinator.com	2014-07-28 16:23:54.898-04	APPLICATION_APPROVAL_STATE_CHANGED	1513	\N	\N	\N	\N
1519	anonymousUser	2014-07-28 16:24:08.097-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1520	anonymousUser	2014-07-28 16:24:09.636-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1521	admin	2014-07-28 16:24:17.313-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1523	admin	2014-07-28 16:24:39.502-04	APPLICATION_APPROVAL_STATE_CHANGED	1513	\N	\N	\N	\N
1524	anonymousUser	2014-07-28 16:24:42.177-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1525	anonymousUser	2014-07-28 16:24:43.215-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1526	able937@mailinator.com	2014-07-28 16:24:54.591-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1527	anonymousUser	2014-07-28 16:29:49.408-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1528	admin	2014-07-28 16:29:53.735-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1529	anonymousUser	2014-07-28 16:30:08.168-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1530	anonymousUser	2014-07-28 16:30:08.817-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1531	anonymousUser	2014-07-28 16:30:09.411-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1532	anonymousUser	2014-07-28 16:30:09.864-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1533	anonymousUser	2014-07-28 16:30:10.05-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1534	anonymousUser	2014-07-28 16:30:10.222-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1535	anonymousUser	2014-07-28 16:30:14.752-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1536	anonymousUser	2014-07-28 16:30:14.754-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1537	anonymousUser	2014-07-28 16:30:14.892-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1538	admin	2014-07-28 16:30:30.581-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1539	anonymousUser	2014-07-28 16:44:13.937-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1540	able937@mailinator.com	2014-07-28 16:44:36.303-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1541	anonymousUser	2014-07-28 16:45:04.371-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1542	anonymousUser	2014-07-28 16:58:54.867-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1543	able937@mailinator.com	2014-07-28 16:59:03.627-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1544	able937@mailinator.com	2014-07-28 17:00:24.029-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1545	able937@mailinator.com	2014-07-28 17:02:15.404-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1546	anonymousUser	2014-07-28 17:02:28.817-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1547	able937@mailinator.com	2014-07-28 17:02:42.472-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1548	able937@mailinator.com	2014-07-28 17:06:39.861-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1549	able937@mailinator.com	2014-07-29 11:14:55.183-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1550	able937@mailinator.com	2014-07-29 11:14:55.183-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1551	anonymousUser	2014-07-29 14:35:20.544-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1552	anonymousUser	2014-07-29 14:35:20.617-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1553	anonymousUser	2014-07-29 14:35:20.665-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1554	able937@mailinator.com	2014-07-29 14:35:36.706-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1559	anonymousUser	2014-07-29 14:39:32.173-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1560	anonymousUser	2014-07-29 14:39:33.787-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1561	admin	2014-07-29 14:39:39.516-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1562	admin	2014-07-29 14:59:48.993-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1563	admin	2014-07-29 15:34:39.978-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1564	anonymousUser	2014-07-29 15:34:47.865-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1565	anonymousUser	2014-07-29 15:34:48.935-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1566	able937@mailinator.com	2014-07-29 15:34:59.497-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1567	able937@mailinator.com	2014-07-29 15:46:02.907-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1568	<unknown>	2014-07-29 15:46:09.099-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
1573	anonymousUser	2014-07-29 15:48:40.874-04	AFFILIATE_CREATED	\N	1571	\N	\N	\N
1574	anonymousUser	2014-07-29 15:48:41.141-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1575	anonymousUser	2014-07-29 15:49:18.389-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1576	anonymousUser	2014-07-29 16:18:14.401-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1577	anonymousUser	2014-07-29 16:18:14.676-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1578	anonymousUser	2014-07-29 16:18:14.838-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1579	anonymousUser	2014-07-29 16:18:14.97-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1580	anonymousUser	2014-07-29 16:18:52.984-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1581	able937@mailinator.com	2014-07-29 16:19:05.727-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1582	anonymousUser	2014-07-29 16:19:11.124-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1583	able938@mailinator.com	2014-07-29 16:19:19.792-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1588	able938@mailinator.com	2014-07-29 16:22:24.107-04	APPLICATION_APPROVAL_STATE_CHANGED	1570	\N	\N	\N	\N
1589	anonymousUser	2014-07-29 16:22:29.209-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1590	anonymousUser	2014-07-29 16:22:30.661-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1591	admin	2014-07-29 16:22:35.033-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1592	admin	2014-07-29 16:22:58.105-04	APPLICATION_APPROVAL_STATE_CHANGED	1570	\N	\N	\N	\N
1593	anonymousUser	2014-07-29 16:23:00.12-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1594	anonymousUser	2014-07-29 16:23:01.193-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1595	able938@mailinator.com	2014-07-29 16:23:14.594-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1596	able938@mailinator.com	2014-07-29 16:24:31.11-04	APPLICATION_APPROVAL_STATE_CHANGED	1570	\N	\N	\N	\N
1597	anonymousUser	2014-07-29 16:36:30.462-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1598	admin	2014-07-29 16:36:33.715-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1599	anonymousUser	2014-07-29 16:36:37.232-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1600	anonymousUser	2014-07-29 16:36:42.623-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1601	admin	2014-07-29 16:36:47.08-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1602	<unknown>	2014-07-29 16:48:41.688-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
1603	anonymousUser	2014-07-29 16:48:42.619-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1604	sweden	2014-07-29 16:48:50.269-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1605	sweden	2014-07-30 09:44:16.209-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1606	anonymousUser	2014-07-30 09:44:46.843-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1607	anonymousUser	2014-07-30 09:44:47.691-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1608	staff	2014-07-30 09:45:23.6-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1609	anonymousUser	2014-07-30 09:59:14.895-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1610	sweden	2014-07-30 09:59:19.806-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1611	anonymousUser	2014-07-30 10:05:15.131-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1612	staff	2014-07-30 10:05:19.884-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1613	anonymousUser	2014-07-30 10:05:27.819-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1614	anonymousUser	2014-07-30 10:05:28.714-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1615	admin	2014-07-30 10:05:31.582-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1616	anonymousUser	2014-07-30 10:07:50.678-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1617	anonymousUser	2014-07-30 10:07:51.898-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1618	sweden	2014-07-30 10:07:56.905-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1619	anonymousUser	2014-07-30 10:08:15.247-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1620	anonymousUser	2014-07-30 10:08:16.31-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1621	staff	2014-07-30 10:08:22.443-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1622	<unknown>	2014-07-30 10:09:01.832-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
1623	anonymousUser	2014-07-30 10:09:02.732-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1624	sweden	2014-07-30 10:09:07.679-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1625	anonymousUser	2014-07-30 10:09:14.5-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1626	anonymousUser	2014-07-30 10:09:16.104-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1627	staff	2014-07-30 10:09:20.036-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1628	anonymousUser	2014-07-30 10:09:29.013-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1629	anonymousUser	2014-07-30 10:09:30.192-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1630	admin	2014-07-30 10:09:33.376-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1631	anonymousUser	2014-07-30 10:40:46.005-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1632	sweden	2014-07-30 10:40:50.803-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1633	anonymousUser	2014-07-30 10:47:49.656-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1634	anonymousUser	2014-07-30 10:47:50.89-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1635	staff	2014-07-30 10:47:53.889-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1636	anonymousUser	2014-07-30 10:48:35.777-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1637	anonymousUser	2014-07-30 10:48:36.838-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1638	admin	2014-07-30 10:48:40.433-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1639	anonymousUser	2014-07-30 10:50:33.811-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1640	anonymousUser	2014-07-30 10:50:34.703-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1641	sweden	2014-07-30 10:50:42.635-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1642	anonymousUser	2014-07-30 10:50:54.765-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1643	anonymousUser	2014-07-30 10:50:55.853-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1644	staff	2014-07-30 10:50:59.717-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1645	staff	2014-07-30 11:30:09.41-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1646	anonymousUser	2014-07-30 11:33:21.905-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1647	anonymousUser	2014-07-30 11:33:24.694-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1648	anonymousUser	2014-07-30 11:33:28.239-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1649	staff	2014-07-30 11:33:31.68-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1650	anonymousUser	2014-07-30 11:34:00.647-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1651	anonymousUser	2014-07-30 11:34:02.428-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1652	able938@mailinator.com	2014-07-30 11:34:14.358-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1653	anonymousUser	2014-07-30 11:34:42.102-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1654	anonymousUser	2014-07-30 11:34:43.47-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1655	sweden	2014-07-30 11:34:47.888-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1656	anonymousUser	2014-07-30 14:11:18.181-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1657	anonymousUser	2014-07-30 14:11:19.75-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1658	able938@mailinator.com	2014-07-30 14:11:31.755-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1659	anonymousUser	2014-07-30 16:03:37.503-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1660	anonymousUser	2014-07-30 16:03:38.877-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1661	sweden	2014-07-30 16:03:49.267-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1662	sweden	2014-07-30 16:04:07.97-04	APPLICATION_APPROVAL_STATE_CHANGED	1383	\N	\N	\N	\N
1663	anonymousUser	2014-07-30 16:04:46.912-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1664	anonymousUser	2014-07-30 16:04:48.31-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1665	staff	2014-07-30 16:04:55.151-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1666	staff	2014-07-30 16:05:04.258-04	APPLICATION_APPROVAL_STATE_CHANGED	1570	\N	\N	\N	\N
1667	staff	2014-07-30 16:08:28.201-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1668	anonymousUser	2014-07-30 17:00:00.009-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1669	anonymousUser	2014-07-30 17:00:01.505-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1670	sweden	2014-07-30 17:00:04.892-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1671	anonymousUser	2014-07-31 09:46:53.839-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1672	anonymousUser	2014-07-31 09:46:55.356-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1673	sweden	2014-07-31 09:46:59.732-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1674	anonymousUser	2014-07-31 09:48:34.748-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1675	anonymousUser	2014-07-31 09:48:35.969-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1676	admin	2014-07-31 09:48:39.019-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1677	anonymousUser	2014-07-31 10:03:05.102-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1678	anonymousUser	2014-07-31 10:03:06.496-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1679	sweden	2014-07-31 10:03:16.72-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1680	anonymousUser	2014-07-31 10:07:36.186-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1681	anonymousUser	2014-07-31 10:07:38.842-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1682	staff	2014-07-31 10:07:43.702-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1683	anonymousUser	2014-07-31 10:07:47.91-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1684	anonymousUser	2014-07-31 10:07:49.317-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1685	sweden	2014-07-31 10:07:53.059-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1686	anonymousUser	2014-07-31 10:09:45.213-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1687	anonymousUser	2014-07-31 10:09:46.724-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1688	admin	2014-07-31 10:09:50.161-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1689	anonymousUser	2014-07-31 10:13:40.996-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1690	anonymousUser	2014-07-31 10:13:42.425-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1691	sweden	2014-07-31 10:13:48.946-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1692	anonymousUser	2014-07-31 10:13:56.91-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1693	anonymousUser	2014-07-31 10:13:58.707-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1694	able938@mailinator.com	2014-07-31 10:14:07.485-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1695	anonymousUser	2014-07-31 10:17:09.707-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1696	anonymousUser	2014-07-31 10:17:10.791-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1697	admin	2014-07-31 10:17:13.753-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1698	anonymousUser	2014-07-31 10:17:48.143-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1699	anonymousUser	2014-07-31 10:17:49.623-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1700	sweden	2014-07-31 10:18:02.547-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1701	anonymousUser	2014-07-31 10:18:10.022-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1702	anonymousUser	2014-07-31 10:18:11.27-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1703	admin	2014-07-31 10:18:22.986-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1705	admin	2014-07-31 10:25:51.422-04	APPLICATION_APPROVAL_STATE_CHANGED	1494	\N	\N	\N	\N
1706	anonymousUser	2014-07-31 10:46:18.499-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1707	anonymousUser	2014-07-31 10:46:25.098-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1708	able938@mailinator.com	2014-07-31 10:46:43.93-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1709	able938@mailinator.com	2014-07-31 11:36:36.871-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1710	anonymousUser	2014-07-31 11:37:21.814-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1711	staff	2014-07-31 11:37:25.253-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1712	<unknown>	2014-07-31 11:37:28.596-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
1713	anonymousUser	2014-07-31 11:37:30.25-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1714	sweden	2014-07-31 11:37:35.398-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1715	anonymousUser	2014-07-31 11:37:37.771-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1716	anonymousUser	2014-07-31 11:37:38.714-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1717	admin	2014-07-31 11:37:43.859-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1718	anonymousUser	2014-07-31 11:37:48.647-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1719	anonymousUser	2014-07-31 11:37:51.799-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1720	able938@mailinator.com	2014-07-31 11:38:02.506-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1721	able938@mailinator.com	2014-07-31 12:02:58.463-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1722	anonymousUser	2014-07-31 12:03:06.282-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1723	sweden	2014-07-31 12:03:09.926-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1724	anonymousUser	2014-07-31 12:03:14.046-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1725	anonymousUser	2014-07-31 12:03:15.286-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1726	staff	2014-07-31 12:03:18.266-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1727	anonymousUser	2014-07-31 12:03:21.169-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1728	anonymousUser	2014-07-31 12:03:22.716-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1729	admin	2014-07-31 12:03:26.048-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1730	anonymousUser	2014-07-31 12:03:28.822-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1735	anonymousUser	2014-07-31 12:03:47.423-04	AFFILIATE_CREATED	\N	1733	\N	\N	\N
1736	anonymousUser	2014-07-31 12:03:47.642-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1737	anonymousUser	2014-07-31 12:04:03.354-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1738	staff	2014-07-31 12:04:09.876-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1739	anonymousUser	2014-07-31 12:04:37.32-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1740	anonymousUser	2014-07-31 12:04:40.039-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1741	anonymousUser	2014-07-31 12:04:46.937-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1742	anonymousUser	2014-07-31 12:04:48.703-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1743	staff	2014-07-31 12:04:51.838-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1744	staff	2014-07-31 12:32:00.794-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1745	staff	2014-07-31 12:44:44.46-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1746	anonymousUser	2014-07-31 12:47:27.572-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1747	anonymousUser	2014-07-31 12:47:29.016-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1748	able939@mailinator.com	2014-07-31 12:47:41.2-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1751	able939@mailinator.com	2014-07-31 13:32:26.733-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1752	able939@mailinator.com	2014-07-31 13:53:19.057-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1753	anonymousUser	2014-07-31 13:55:20.478-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1754	anonymousUser	2014-07-31 13:55:26.498-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1755	admin	2014-07-31 13:55:31.708-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1756	admin	2014-07-31 13:55:53.764-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1757	anonymousUser	2014-07-31 13:55:56.242-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1758	anonymousUser	2014-07-31 13:55:58.122-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1759	able939@mailinator.com	2014-07-31 13:56:11.02-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1760	able939@mailinator.com	2014-07-31 13:56:40.102-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1761	anonymousUser	2014-07-31 13:56:44.055-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1762	anonymousUser	2014-07-31 13:56:47.308-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1763	sweden	2014-07-31 13:56:51.948-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1764	anonymousUser	2014-07-31 13:57:19.936-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1765	anonymousUser	2014-07-31 13:57:21.434-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1766	able939@mailinator.com	2014-07-31 13:57:29.685-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1767	anonymousUser	2014-07-31 13:58:37.353-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1769	sweden	2014-07-31 13:58:43.692-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1770	sweden	2014-07-31 13:59:02.131-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1772	anonymousUser	2014-07-31 13:59:06.07-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1773	able939@mailinator.com	2014-07-31 13:59:17.421-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1768	anonymousUser	2014-07-31 13:58:38.883-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1771	anonymousUser	2014-07-31 13:59:04.68-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1774	able939@mailinator.com	2014-07-31 14:00:40.548-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1775	anonymousUser	2014-07-31 14:17:17.335-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1776	anonymousUser	2014-07-31 14:17:18.675-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1777	staff	2014-07-31 14:17:21.947-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1778	anonymousUser	2014-07-31 14:17:26.539-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1779	anonymousUser	2014-07-31 14:17:27.888-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1780	sweden	2014-07-31 14:17:33.187-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1781	anonymousUser	2014-07-31 14:17:59.611-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1782	anonymousUser	2014-07-31 14:18:01.377-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1783	staff	2014-07-31 14:18:09.057-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1784	staff	2014-07-31 14:18:15.365-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1785	anonymousUser	2014-07-31 14:18:17.144-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1786	anonymousUser	2014-07-31 14:18:18.534-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1787	able939@mailinator.com	2014-07-31 14:18:31.589-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1788	able939@mailinator.com	2014-07-31 14:25:33.546-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1789	anonymousUser	2014-07-31 14:25:41.569-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1790	anonymousUser	2014-07-31 14:25:42.937-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1791	staff	2014-07-31 14:25:46.414-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1792	staff	2014-07-31 14:27:12.795-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1793	anonymousUser	2014-07-31 14:27:15.611-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1794	anonymousUser	2014-07-31 14:27:16.985-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1795	able939@mailinator.com	2014-07-31 14:27:26.718-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1798	able939@mailinator.com	2014-07-31 14:40:14.037-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1799	anonymousUser	2014-07-31 14:40:19.17-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1800	anonymousUser	2014-07-31 14:40:20.33-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1801	staff	2014-07-31 14:40:24.435-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1802	staff	2014-07-31 14:40:46.522-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1803	anonymousUser	2014-07-31 14:41:33.488-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1804	anonymousUser	2014-07-31 14:41:35.154-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1805	able939@mailinator.com	2014-07-31 14:41:44.239-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1806	able939@mailinator.com	2014-07-31 14:58:41.329-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1807	anonymousUser	2014-07-31 15:02:02.907-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1808	anonymousUser	2014-07-31 15:02:03.786-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1809	staff	2014-07-31 15:02:09.037-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1811	staff	2014-07-31 15:02:14.978-04	APPLICATION_APPROVAL_STATE_CHANGED	1732	\N	\N	\N	\N
1812	anonymousUser	2014-07-31 15:02:18.094-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1813	anonymousUser	2014-07-31 15:06:27.844-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1814	able939@mailinator.com	2014-07-31 15:07:04.131-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1815	anonymousUser	2014-07-31 15:16:17.125-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1816	anonymousUser	2014-07-31 15:16:18.026-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1817	anonymousUser	2014-07-31 15:16:23.225-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1822	anonymousUser	2014-07-31 15:16:42.48-04	AFFILIATE_CREATED	\N	1820	\N	\N	\N
1823	anonymousUser	2014-07-31 15:16:42.561-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1824	anonymousUser	2014-07-31 15:16:57.959-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1825	able940@mailinator.com	2014-07-31 15:18:27.63-04	APPLICATION_APPROVAL_STATE_CHANGED	1819	\N	\N	\N	\N
1826	anonymousUser	2014-07-31 15:18:42.026-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1827	anonymousUser	2014-07-31 15:18:43.36-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1828	sweden	2014-07-31 15:18:47.568-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1830	sweden	2014-07-31 15:19:00.005-04	APPLICATION_APPROVAL_STATE_CHANGED	1819	\N	\N	\N	\N
1831	anonymousUser	2014-07-31 15:19:05.289-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1832	anonymousUser	2014-07-31 15:19:06.563-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1833	able940@mailinator.com	2014-07-31 15:19:24.313-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1834	anonymousUser	2014-07-31 15:32:19.427-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1835	anonymousUser	2014-07-31 15:32:20.792-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1836	anonymousUser	2014-07-31 15:32:22.139-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1837	anonymousUser	2014-07-31 15:32:22.722-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1842	anonymousUser	2014-07-31 15:32:51.393-04	AFFILIATE_CREATED	\N	1840	\N	\N	\N
1843	anonymousUser	2014-07-31 15:32:51.441-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1844	anonymousUser	2014-07-31 15:33:02.293-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1845	able941@mailinator.com	2014-07-31 15:33:52.306-04	APPLICATION_APPROVAL_STATE_CHANGED	1839	\N	\N	\N	\N
1846	anonymousUser	2014-07-31 15:34:06.912-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1847	anonymousUser	2014-07-31 15:34:08.204-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1848	staff	2014-07-31 15:34:12.374-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1849	anonymousUser	2014-07-31 15:34:27.984-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1850	anonymousUser	2014-07-31 15:34:29.494-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1851	sweden	2014-07-31 15:34:35.658-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1853	sweden	2014-07-31 15:34:44.327-04	APPLICATION_APPROVAL_STATE_CHANGED	1839	\N	\N	\N	\N
1854	anonymousUser	2014-07-31 15:34:47.766-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1855	anonymousUser	2014-07-31 15:34:49.662-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1856	able941@mailinator.com	2014-07-31 15:35:02.876-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1857	anonymousUser	2014-07-31 15:35:23.589-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1862	anonymousUser	2014-07-31 15:35:39.963-04	AFFILIATE_CREATED	\N	1860	\N	\N	\N
1863	anonymousUser	2014-07-31 15:35:40-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1864	anonymousUser	2014-07-31 15:35:50.46-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1865	able942@mailinator.com	2014-07-31 15:36:54.192-04	APPLICATION_APPROVAL_STATE_CHANGED	1859	\N	\N	\N	\N
1866	anonymousUser	2014-07-31 15:37:04.557-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1867	anonymousUser	2014-07-31 15:37:05.641-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1868	sweden	2014-07-31 15:37:09.86-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1870	sweden	2014-07-31 15:37:22.072-04	APPLICATION_APPROVAL_STATE_CHANGED	1859	\N	\N	\N	\N
1871	anonymousUser	2014-07-31 15:37:28.18-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1872	anonymousUser	2014-07-31 15:37:29.297-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1873	able942@mailinator.com	2014-07-31 15:37:40.99-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1874	anonymousUser	2014-07-31 15:42:24.995-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1875	anonymousUser	2014-07-31 15:42:26.509-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1876	able938@mailinator.com	2014-07-31 15:42:37.092-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1877	anonymousUser	2014-07-31 15:52:51.764-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1878	anonymousUser	2014-07-31 15:52:53.005-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1879	admin	2014-07-31 15:53:06.151-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1880	admin	2014-07-31 15:53:16.058-04	APPLICATION_APPROVAL_STATE_CHANGED	1570	\N	\N	\N	\N
1881	anonymousUser	2014-07-31 15:53:19.407-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1882	anonymousUser	2014-07-31 15:53:22.007-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1883	able938@mailinator.com	2014-07-31 15:53:38.305-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1884	able938@mailinator.com	2014-07-31 15:57:27.502-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1885	able938@mailinator.com	2014-07-31 17:16:57.44-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1886	able938@mailinator.com	2014-08-01 09:33:50.324-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1887	anonymousUser	2014-08-01 09:33:57.658-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1888	anonymousUser	2014-08-01 09:33:59.199-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1889	sweden	2014-08-01 09:34:04.544-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1890	sweden	2014-08-01 10:06:27.435-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1892	sweden	2014-08-01 10:06:55.544-04	RELEASE_PACKAGE_CREATED	\N	\N	1891	\N	\N
1894	sweden	2014-08-01 10:07:15.493-04	RELEASE_VERSION_CREATED	\N	\N	1891	1893	\N
1896	sweden	2014-08-01 10:07:24.875-04	RELEASE_FILE_CREATED	\N	\N	1891	1893	1895
1898	sweden	2014-08-01 10:07:32.138-04	RELEASE_FILE_CREATED	\N	\N	1891	1893	1897
1900	sweden	2014-08-01 10:07:43.921-04	RELEASE_VERSION_CREATED	\N	\N	1891	1899	\N
1902	sweden	2014-08-01 10:07:56.011-04	RELEASE_FILE_CREATED	\N	\N	1891	1899	1901
1904	sweden	2014-08-01 10:08:01.964-04	RELEASE_FILE_CREATED	\N	\N	1891	1899	1903
1905	sweden	2014-08-01 10:08:09.051-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1891	1899	\N
1906	sweden	2014-08-01 10:08:13.48-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1891	1893	\N
1907	sweden	2014-08-01 10:08:26.12-04	RELEASE_VERSION_TAKEN_OFFLINE	\N	\N	1891	1899	\N
1908	sweden	2014-08-01 10:08:29.784-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1891	1899	\N
1910	sweden	2014-08-01 10:08:52.52-04	RELEASE_PACKAGE_CREATED	\N	\N	1909	\N	\N
1912	sweden	2014-08-01 10:09:04.421-04	RELEASE_VERSION_CREATED	\N	\N	1909	1911	\N
1914	sweden	2014-08-01 10:09:12.043-04	RELEASE_FILE_CREATED	\N	\N	1909	1911	1913
1915	sweden	2014-08-01 10:09:21.284-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	1909	1911	\N
1917	sweden	2014-08-01 10:09:42.388-04	RELEASE_PACKAGE_CREATED	\N	\N	1916	\N	\N
1919	sweden	2014-08-01 10:09:52.21-04	RELEASE_VERSION_CREATED	\N	\N	1916	1918	\N
1921	sweden	2014-08-01 10:09:58.555-04	RELEASE_FILE_CREATED	\N	\N	1916	1918	1920
1922	anonymousUser	2014-08-01 10:10:19.902-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1923	anonymousUser	2014-08-01 10:10:21.716-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1924	anonymousUser	2014-08-01 10:10:29.589-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1925	anonymousUser	2014-08-01 10:11:44.678-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1926	able940@mailinator.com	2014-08-01 10:11:54.963-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1927	able940@mailinator.com	2014-08-01 11:35:43.86-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1928	anonymousUser	2014-08-01 11:36:34.409-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1929	anonymousUser	2014-08-01 11:36:35.404-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1930	admin	2014-08-01 11:36:38.755-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1931	anonymousUser	2014-08-01 13:57:13.313-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1932	anonymousUser	2014-08-01 13:57:29.304-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1933	able939@mailinator.com	2014-08-01 13:57:48.979-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1934	able939@mailinator.com	2014-08-01 14:46:48.062-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1935	sweden	2014-08-01 16:15:49.628-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1936	sweden	2014-08-01 16:15:49.659-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1937	sweden	2014-08-01 16:16:06.058-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1938	anonymousUser	2014-08-01 16:16:06.099-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1939	anonymousUser	2014-08-01 16:16:06.155-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1940	staff	2014-08-01 16:16:09.127-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1941	staff	2014-08-01 16:16:09.15-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1942	staff	2014-08-01 16:16:18.656-04	APPLICATION_APPROVAL_STATE_CHANGED	1513	\N	\N	\N	\N
1943	staff	2014-08-01 16:17:59.866-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1944	anonymousUser	2014-08-01 16:17:59.895-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1945	anonymousUser	2014-08-01 16:17:59.941-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1946	able939@mailinator.com	2014-08-01 16:18:18.994-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1947	anonymousUser	2014-08-01 16:37:10.502-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1948	anonymousUser	2014-08-01 16:37:14.511-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1949	sweden	2014-08-01 16:37:19.061-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1950	sweden	2014-08-01 16:37:19.084-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1951	sweden	2014-08-01 16:37:28.274-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1952	anonymousUser	2014-08-01 16:37:28.31-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1953	anonymousUser	2014-08-01 16:37:28.36-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1954	anonymousUser	2014-08-01 16:37:28.405-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1955	able940@mailinator.com	2014-08-01 16:37:37.917-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1956	anonymousUser	2014-08-01 16:44:05.922-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1957	anonymousUser	2014-08-01 16:44:07.77-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1958	staff	2014-08-01 16:44:11.633-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1959	staff	2014-08-01 16:44:11.68-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1960	staff	2014-08-01 16:44:25.734-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1961	anonymousUser	2014-08-01 16:44:25.801-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1962	anonymousUser	2014-08-01 16:44:25.843-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1963	able937@mailinator.com	2014-08-01 16:44:35.796-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1964	anonymousUser	2014-08-01 17:04:54.111-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1965	able940@mailinator.com	2014-08-01 17:05:04.043-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1966	anonymousUser	2014-08-01 17:05:23.566-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1967	anonymousUser	2014-08-01 17:05:24.313-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1968	anonymousUser	2014-08-01 17:05:25.469-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1969	anonymousUser	2014-08-01 17:05:26.824-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1970	anonymousUser	2014-08-01 17:05:28.46-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1971	anonymousUser	2014-08-05 10:53:23.599-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1972	anonymousUser	2014-08-05 10:53:23.688-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1973	anonymousUser	2014-08-05 10:53:25.295-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1974	anonymousUser	2014-08-05 10:53:26.955-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1976	anonymousUser	2014-08-05 10:56:05.501-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1977	anonymousUser	2014-08-05 10:56:05.575-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1980	able939@mailinator.com	2014-08-05 10:56:42.981-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
1975	anonymousUser	2014-08-05 10:53:26.976-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1978	anonymousUser	2014-08-05 10:56:05.577-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1979	anonymousUser	2014-08-05 10:56:30.203-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1981	anonymousUser	2014-08-05 11:09:25.3-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1982	anonymousUser	2014-08-05 11:09:28.017-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1984	anonymousUser	2014-08-05 11:09:28.096-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1983	anonymousUser	2014-08-05 11:09:28.096-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1985	anonymousUser	2014-08-05 11:09:43.368-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1986	anonymousUser	2014-08-05 11:09:43.437-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1987	anonymousUser	2014-08-05 11:09:43.438-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1988	anonymousUser	2014-08-05 11:12:11.639-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1989	anonymousUser	2014-08-05 11:12:11.712-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1990	anonymousUser	2014-08-05 11:12:11.713-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1991	anonymousUser	2014-08-05 11:17:14.035-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1993	anonymousUser	2014-08-05 11:17:14.13-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1992	anonymousUser	2014-08-05 11:17:14.127-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1994	anonymousUser	2014-08-05 11:23:03.127-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1996	anonymousUser	2014-08-05 11:23:03.24-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1995	anonymousUser	2014-08-05 11:23:03.238-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1997	anonymousUser	2014-08-05 13:41:43.555-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1998	anonymousUser	2014-08-05 13:41:46.097-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
1999	anonymousUser	2014-08-05 13:43:33.652-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2000	anonymousUser	2014-08-05 13:45:58.686-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2001	anonymousUser	2014-08-05 13:47:44.384-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2002	anonymousUser	2014-08-05 13:49:56.658-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2003	anonymousUser	2014-08-05 13:51:12.218-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2004	anonymousUser	2014-08-05 13:51:36.434-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2005	anonymousUser	2014-08-05 13:52:43.672-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2006	anonymousUser	2014-08-05 13:53:55.897-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2007	anonymousUser	2014-08-05 13:54:21.559-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2012	anonymousUser	2014-08-05 13:57:02.424-04	AFFILIATE_CREATED	\N	2008	\N	\N	\N
2013	anonymousUser	2014-08-05 13:57:02.658-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2014	anonymousUser	2014-08-05 14:03:39.532-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2015	anonymousUser	2014-08-05 14:03:41.89-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2016	anonymousUser	2014-08-05 14:03:43.962-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2017	anonymousUser	2014-08-05 14:03:45.066-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2018	anonymousUser	2014-08-05 14:04:03.386-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2019	anonymousUser	2014-08-05 14:04:28.12-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2020	anonymousUser	2014-08-05 14:07:13.647-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2021	anonymousUser	2014-08-05 14:08:15.239-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2022	anonymousUser	2014-08-05 14:10:58.443-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2023	anonymousUser	2014-08-05 14:11:52.991-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2024	anonymousUser	2014-08-05 14:16:11.393-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2025	anonymousUser	2014-08-05 14:17:45.662-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2026	anonymousUser	2014-08-05 14:24:24.62-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2027	anonymousUser	2014-08-05 14:26:34.274-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2028	anonymousUser	2014-08-05 14:28:09.551-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2029	anonymousUser	2014-08-05 14:28:25.769-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2030	anonymousUser	2014-08-05 14:29:29.868-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2031	anonymousUser	2014-08-05 14:30:16.586-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2032	anonymousUser	2014-08-05 14:31:05.282-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2037	anonymousUser	2014-08-05 14:32:03.217-04	AFFILIATE_CREATED	\N	2033	\N	\N	\N
2038	anonymousUser	2014-08-05 14:32:03.261-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2039	anonymousUser	2014-08-05 14:32:21.237-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2040	anonymousUser	2014-08-05 14:33:48.33-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2041	anonymousUser	2014-08-05 14:33:48.346-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2042	anonymousUser	2014-08-05 14:33:50.293-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2043	anonymousUser	2014-08-05 14:35:36.718-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2044	admin	2014-08-05 14:35:39.948-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2050	anonymousUser	2014-08-05 14:43:19.822-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2051	anonymousUser	2014-08-05 14:43:21.558-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2052	anonymousUser	2014-08-05 14:43:23.32-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2057	anonymousUser	2014-08-05 14:43:46.643-04	AFFILIATE_CREATED	\N	2053	\N	\N	\N
2058	anonymousUser	2014-08-05 14:43:46.691-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2059	anonymousUser	2014-08-05 14:44:49.425-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2060	anonymousUser	2014-08-05 14:44:50.422-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2061	anonymousUser	2014-08-05 14:44:50.538-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2062	anonymousUser	2014-08-05 14:44:54.143-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2063	anonymousUser	2014-08-05 14:48:46.813-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2064	staff	2014-08-05 14:48:51.696-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2065	staff	2014-08-05 14:48:51.765-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2066	staff	2014-08-05 14:48:58.409-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2067	anonymousUser	2014-08-05 14:48:59.797-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2068	sweden	2014-08-05 14:49:04.043-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2069	sweden	2014-08-05 14:49:04.068-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2070	anonymousUser	2014-08-05 14:51:41.689-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2071	anonymousUser	2014-08-05 14:51:43.025-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2072	staff	2014-08-05 14:51:46.08-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2073	anonymousUser	2014-08-05 14:51:47.915-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2074	anonymousUser	2014-08-05 14:51:49.021-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2075	admin	2014-08-05 14:51:51.955-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2076	anonymousUser	2014-08-05 14:52:35.105-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2077	admin	2014-08-05 14:52:38.34-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2078	anonymousUser	2014-08-05 14:52:40.077-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2079	anonymousUser	2014-08-05 14:52:40.998-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2080	sweden	2014-08-05 14:52:44.123-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2081	anonymousUser	2014-08-05 14:52:45.868-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2082	anonymousUser	2014-08-05 14:52:47.643-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2083	able939@mailinator.com	2014-08-05 14:52:57.804-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2084	anonymousUser	2014-08-05 14:57:40.809-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2085	anonymousUser	2014-08-05 14:57:42.057-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2086	staff	2014-08-05 14:57:45.334-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2087	staff	2014-08-05 14:57:45.383-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2088	staff	2014-08-05 14:57:58.607-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2092	anonymousUser	2014-08-05 15:01:00.479-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2093	anonymousUser	2014-08-05 15:01:01.678-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2095	sweden	2014-08-05 15:01:07.665-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2096	anonymousUser	2014-08-05 15:01:24.358-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2099	anonymousUser	2014-08-05 15:01:40.564-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2100	anonymousUser	2014-08-05 15:01:45.149-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2103	staff	2014-08-05 15:02:25.081-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2105	anonymousUser	2014-08-05 15:03:58.846-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2108	able939@mailinator.com	2014-08-05 15:07:53.447-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2089	anonymousUser	2014-08-05 14:57:58.637-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2090	anonymousUser	2014-08-05 14:57:58.686-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2091	able939@mailinator.com	2014-08-05 14:58:22.837-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2097	anonymousUser	2014-08-05 15:01:25.692-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2101	able939@mailinator.com	2014-08-05 15:01:54.552-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2102	anonymousUser	2014-08-05 15:02:21.883-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2104	staff	2014-08-05 15:02:25.105-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2106	anonymousUser	2014-08-05 15:03:59.851-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2107	staff	2014-08-05 15:04:02.71-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2094	sweden	2014-08-05 15:01:07.641-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2098	admin	2014-08-05 15:01:30.73-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2109	able939@mailinator.com	2014-08-05 15:21:36.586-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2110	able939@mailinator.com	2014-08-05 15:36:53.264-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2111	able939@mailinator.com	2014-08-05 15:39:04.505-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2112	anonymousUser	2014-08-05 15:44:12.11-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2113	sweden	2014-08-05 15:44:15.871-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2114	sweden	2014-08-05 15:44:15.9-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2115	anonymousUser	2014-08-05 16:16:21.435-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2116	anonymousUser	2014-08-05 16:16:22.669-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2117	able939@mailinator.com	2014-08-05 16:16:38.35-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2118	anonymousUser	2014-08-05 16:17:25.941-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2119	anonymousUser	2014-08-05 16:17:27.748-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2124	anonymousUser	2014-08-05 16:18:05.881-04	AFFILIATE_CREATED	\N	2120	\N	\N	\N
2125	anonymousUser	2014-08-05 16:18:05.968-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2126	anonymousUser	2014-08-05 16:18:20.181-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2127	anonymousUser	2014-08-05 17:11:21.174-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2128	anonymousUser	2014-08-05 17:11:21.201-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2129	anonymousUser	2014-08-05 17:11:26.797-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2134	anonymousUser	2014-08-05 17:11:50.305-04	AFFILIATE_CREATED	\N	2130	\N	\N	\N
2135	anonymousUser	2014-08-05 17:11:50.362-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2136	anonymousUser	2014-08-05 17:12:03.279-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2140	able946@mailinator.com	2014-08-05 17:13:48.961-04	APPLICATION_APPROVAL_STATE_CHANGED	2132	\N	\N	\N	\N
2141	<unknown>	2014-08-05 17:13:56.785-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2142	anonymousUser	2014-08-05 17:13:56.803-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2143	anonymousUser	2014-08-05 17:13:58.508-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2144	sweden	2014-08-05 17:14:03.147-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2145	sweden	2014-08-05 17:14:03.176-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2147	sweden	2014-08-05 17:14:18.045-04	APPLICATION_APPROVAL_STATE_CHANGED	2132	\N	\N	\N	\N
2148	sweden	2014-08-05 17:14:21.042-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2149	anonymousUser	2014-08-05 17:14:21.066-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2150	anonymousUser	2014-08-05 17:14:21.104-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2151	able946@mailinator.com	2014-08-05 17:14:31.943-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2152	able946@mailinator.com	2014-08-06 09:58:33.27-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2162	anonymousUser	2014-08-06 10:00:00.222-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2163	sweden	2014-08-06 10:00:04.579-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2164	sweden	2014-08-06 10:00:04.609-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2165	sweden	2014-08-06 10:00:38.315-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2166	anonymousUser	2014-08-06 10:00:43.025-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2167	anonymousUser	2014-08-06 10:00:43.047-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2168	anonymousUser	2014-08-06 10:00:44.257-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2173	anonymousUser	2014-08-06 10:01:10.889-04	AFFILIATE_CREATED	\N	2169	\N	\N	\N
2174	anonymousUser	2014-08-06 10:01:11.09-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2175	anonymousUser	2014-08-06 10:01:22.178-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2191	able947@mailinator.com	2014-08-06 10:02:22.527-04	APPLICATION_APPROVAL_STATE_CHANGED	2171	\N	\N	\N	\N
2192	<unknown>	2014-08-06 10:02:25.963-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2193	anonymousUser	2014-08-06 10:02:25.988-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2194	anonymousUser	2014-08-06 10:02:27.159-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2195	sweden	2014-08-06 10:02:34.093-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2196	sweden	2014-08-06 10:02:34.12-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2197	sweden	2014-08-06 10:02:46.46-04	APPLICATION_APPROVAL_STATE_CHANGED	2171	\N	\N	\N	\N
2198	sweden	2014-08-06 10:02:48.464-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2199	anonymousUser	2014-08-06 10:02:48.489-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2200	anonymousUser	2014-08-06 10:02:48.526-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2201	able947@mailinator.com	2014-08-06 10:02:57.241-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2202	able947@mailinator.com	2014-08-06 10:03:08.412-04	APPLICATION_APPROVAL_STATE_CHANGED	2171	\N	\N	\N	\N
2203	anonymousUser	2014-08-06 10:03:11.039-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2204	anonymousUser	2014-08-06 10:03:12.276-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2205	sweden	2014-08-06 10:03:18.438-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2206	sweden	2014-08-06 10:03:18.461-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2208	sweden	2014-08-06 10:03:24.25-04	APPLICATION_APPROVAL_STATE_CHANGED	2171	\N	\N	\N	\N
2209	sweden	2014-08-06 10:03:36.533-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2210	anonymousUser	2014-08-06 10:03:36.566-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2211	anonymousUser	2014-08-06 10:03:36.621-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2212	admin	2014-08-06 10:03:40.527-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2213	admin	2014-08-06 10:35:02.016-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2215	anonymousUser	2014-08-06 10:36:15.834-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2216	sweden	2014-08-06 10:36:20.844-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2217	anonymousUser	2014-08-06 10:36:28.021-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2222	anonymousUser	2014-08-06 10:36:52.59-04	AFFILIATE_CREATED	\N	2218	\N	\N	\N
2223	anonymousUser	2014-08-06 10:36:52.796-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2224	anonymousUser	2014-08-06 10:37:08.624-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2226	able948@mailinator.com	2014-08-06 10:38:47.717-04	APPLICATION_APPROVAL_STATE_CHANGED	2220	\N	\N	\N	\N
2227	<unknown>	2014-08-06 10:38:56.073-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2228	anonymousUser	2014-08-06 10:38:56.092-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2229	anonymousUser	2014-08-06 10:38:57.338-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2230	sweden	2014-08-06 10:39:00.957-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2231	sweden	2014-08-06 10:39:00.981-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2232	sweden	2014-08-06 10:39:31.628-04	APPLICATION_APPROVAL_STATE_CHANGED	2220	\N	\N	\N	\N
2233	sweden	2014-08-06 10:39:33.436-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2234	anonymousUser	2014-08-06 10:39:36.625-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2235	anonymousUser	2014-08-06 10:39:36.646-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2236	anonymousUser	2014-08-06 10:39:38.223-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2237	able947@mailinator.com	2014-08-06 10:39:50.125-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2238	anonymousUser	2014-08-06 10:40:04.553-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2239	anonymousUser	2014-08-06 10:40:05.854-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2240	able948@mailinator.com	2014-08-06 10:40:15.105-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2242	able948@mailinator.com	2014-08-06 10:40:32.09-04	APPLICATION_APPROVAL_STATE_CHANGED	2220	\N	\N	\N	\N
2243	staff	2014-08-06 10:40:53.226-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2244	staff	2014-08-06 10:40:53.248-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2245	staff	2014-08-06 10:40:59.045-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2247	anonymousUser	2014-08-06 10:40:59.124-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2252	anonymousUser	2014-08-06 10:41:16.022-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2255	<unknown>	2014-08-06 10:41:29.697-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2259	anonymousUser	2014-08-06 10:43:36.24-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2260	anonymousUser	2014-08-06 10:44:14.515-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2246	anonymousUser	2014-08-06 10:40:59.084-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2248	admin	2014-08-06 10:41:02.099-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2250	admin	2014-08-06 10:41:09.877-04	APPLICATION_APPROVAL_STATE_CHANGED	2220	\N	\N	\N	\N
2251	anonymousUser	2014-08-06 10:41:15.989-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2253	anonymousUser	2014-08-06 10:41:16.731-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2254	able948@mailinator.com	2014-08-06 10:41:26.013-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2256	anonymousUser	2014-08-06 10:41:31.333-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2257	anonymousUser	2014-08-06 10:41:32.354-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2258	anonymousUser	2014-08-06 10:41:40.805-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2261	able948@mailinator.com	2014-08-06 10:44:22.707-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2262	able948@mailinator.com	2014-08-06 10:54:45.78-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2263	staff	2014-08-06 10:55:29.296-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2264	staff	2014-08-06 10:55:29.328-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2265	anonymousUser	2014-08-06 10:59:25.57-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2266	anonymousUser	2014-08-06 10:59:25.57-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2267	anonymousUser	2014-08-06 10:59:29.176-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2268	anonymousUser	2014-08-06 10:59:32.503-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2269	anonymousUser	2014-08-06 10:59:43.571-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2270	anonymousUser	2014-08-06 10:59:47.967-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2271	anonymousUser	2014-08-06 12:37:36.062-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2272	anonymousUser	2014-08-06 12:38:09.53-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2273	anonymousUser	2014-08-06 12:38:49.529-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2274	anonymousUser	2014-08-06 12:38:54.474-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2275	anonymousUser	2014-08-06 12:38:57.227-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2276	anonymousUser	2014-08-06 12:40:40.716-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2277	anonymousUser	2014-08-06 12:47:41.997-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2278	anonymousUser	2014-08-06 12:48:32.786-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2279	anonymousUser	2014-08-06 12:48:35.924-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2280	anonymousUser	2014-08-06 12:48:43.924-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2281	anonymousUser	2014-08-06 12:48:51.786-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2282	anonymousUser	2014-08-06 12:49:10.355-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2283	anonymousUser	2014-08-06 12:49:17.582-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2284	anonymousUser	2014-08-06 12:49:26.4-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2285	anonymousUser	2014-08-06 12:49:36.972-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2286	anonymousUser	2014-08-06 12:52:39.682-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2287	anonymousUser	2014-08-06 12:57:54.184-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2288	anonymousUser	2014-08-06 12:59:05.728-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2289	anonymousUser	2014-08-06 12:59:55.752-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2290	anonymousUser	2014-08-06 13:00:39.298-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2291	anonymousUser	2014-08-06 13:00:55.1-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2292	able947@mailinator.com	2014-08-06 13:03:57.718-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2293	<unknown>	2014-08-06 13:18:05.791-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2294	anonymousUser	2014-08-06 13:18:06.934-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2295	able947@mailinator.com	2014-08-06 13:18:16.23-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2300	anonymousUser	2014-08-06 13:19:32.106-04	AFFILIATE_CREATED	\N	2296	\N	\N	\N
2301	anonymousUser	2014-08-06 13:20:03.208-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2302	anonymousUser	2014-08-06 13:20:04.343-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2303	able949@mailinator.com	2014-08-06 13:20:12.849-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2304	able949@mailinator.com	2014-08-06 14:14:25.292-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2305	able949@mailinator.com	2014-08-06 14:46:05.088-04	APPLICATION_APPROVAL_STATE_CHANGED	2298	\N	\N	\N	\N
2306	anonymousUser	2014-08-06 16:01:13.875-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2307	admin	2014-08-06 16:01:17.005-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2311	af	2014-08-06 16:14:28.326-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2312	af	2014-08-06 16:16:18.459-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2313	af	2014-08-06 16:16:25.879-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2314	af	2014-08-06 16:18:13.765-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2315	af	2014-08-06 16:18:59.299-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2316	anonymousUser	2014-08-06 16:19:09.83-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2317	anonymousUser	2014-08-06 16:19:10.853-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2318	af	2014-08-06 16:19:13.924-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2319	af	2014-08-06 16:19:20.708-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2321	af	2014-08-06 16:19:34.489-04	RELEASE_PACKAGE_CREATED	\N	\N	2320	\N	\N
2323	af	2014-08-06 16:19:43.504-04	RELEASE_VERSION_CREATED	\N	\N	2320	2322	\N
2325	af	2014-08-06 16:19:53.615-04	RELEASE_FILE_CREATED	\N	\N	2320	2322	2324
2327	af	2014-08-06 16:20:00.189-04	RELEASE_FILE_CREATED	\N	\N	2320	2322	2326
2328	af	2014-08-06 16:20:07.183-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	2320	2322	\N
2330	af	2014-08-06 16:20:15.677-04	RELEASE_VERSION_CREATED	\N	\N	2320	2329	\N
2332	af	2014-08-06 16:20:23.39-04	RELEASE_FILE_CREATED	\N	\N	2320	2329	2331
2333	af	2014-08-06 16:20:28.028-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2334	anonymousUser	2014-08-06 16:20:28.059-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2335	anonymousUser	2014-08-06 16:20:28.101-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2336	fr	2014-08-06 16:20:31.223-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2337	fr	2014-08-06 16:20:31.249-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2339	fr	2014-08-06 16:20:46.888-04	RELEASE_PACKAGE_CREATED	\N	\N	2338	\N	\N
2341	fr	2014-08-06 16:20:55.926-04	RELEASE_VERSION_CREATED	\N	\N	2338	2340	\N
2343	fr	2014-08-06 16:21:02.252-04	RELEASE_FILE_CREATED	\N	\N	2338	2340	2342
2345	fr	2014-08-06 16:21:08.989-04	RELEASE_FILE_CREATED	\N	\N	2338	2340	2344
2346	fr	2014-08-06 16:21:12.807-04	RELEASE_VERSION_TAKEN_ONLINE	\N	\N	2338	2340	\N
2347	fr	2014-08-06 16:21:14.896-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2348	anonymousUser	2014-08-06 16:21:14.922-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2349	anonymousUser	2014-08-06 16:21:14.963-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2350	able939@mailinator.com	2014-08-06 16:21:37.815-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2351	anonymousUser	2014-08-07 09:17:18.823-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2352	admin	2014-08-07 09:17:22.36-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2353	anonymousUser	2014-08-07 09:17:39.727-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2354	anonymousUser	2014-08-07 09:17:41.038-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2355	able949@mailinator.com	2014-08-07 09:17:51.964-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2356	anonymousUser	2014-08-08 09:33:14.045-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2357	admin	2014-08-08 09:33:17.361-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2359	admin	2014-08-08 09:33:25.949-04	APPLICATION_APPROVAL_STATE_CHANGED	2298	\N	\N	\N	\N
2360	anonymousUser	2014-08-08 09:33:46.024-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2361	anonymousUser	2014-08-08 09:33:49.371-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2362	able949@mailinator.com	2014-08-08 09:34:01.191-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2363	anonymousUser	2014-08-08 10:22:13.958-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2364	anonymousUser	2014-08-08 10:22:13.958-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2365	anonymousUser	2014-08-08 10:22:14.863-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2366	anonymousUser	2014-08-08 10:22:22.167-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2371	anonymousUser	2014-08-08 10:22:38.675-04	AFFILIATE_CREATED	\N	2367	\N	\N	\N
2372	anonymousUser	2014-08-08 10:22:38.903-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2373	anonymousUser	2014-08-08 10:22:48.251-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2376	able950@mailinator.com	2014-08-08 10:25:22.971-04	APPLICATION_APPROVAL_STATE_CHANGED	2369	\N	\N	\N	\N
2377	anonymousUser	2014-08-08 10:25:43.264-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2378	anonymousUser	2014-08-08 10:25:43.283-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2379	anonymousUser	2014-08-08 10:25:44.565-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2380	admin	2014-08-08 10:25:47.55-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2381	admin	2014-08-08 10:41:30.15-04	APPLICATION_APPROVAL_STATE_CHANGED	2369	\N	\N	\N	\N
2382	anonymousUser	2014-08-08 10:41:32.94-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2383	anonymousUser	2014-08-08 10:41:33.948-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2384	able950@mailinator.com	2014-08-08 10:41:48.781-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2386	<unknown>	2014-08-08 11:34:18.858-04	AUTHENTICATION_FAILURE	\N	\N	\N	\N	\N
2387	anonymousUser	2014-08-08 11:34:20.357-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2388	able949@mailinator.com	2014-08-08 11:34:30.28-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2391	anonymousUser	2014-08-08 12:10:09.616-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2392	anonymousUser	2014-08-08 12:10:10.688-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2393	admin	2014-08-08 12:10:14.006-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2394	anonymousUser	2014-08-08 12:20:49.002-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2395	anonymousUser	2014-08-08 12:20:50.065-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2396	staff	2014-08-08 12:20:52.965-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2397	anonymousUser	2014-08-08 12:20:55.611-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2398	anonymousUser	2014-08-08 12:20:56.624-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2399	admin	2014-08-08 12:21:00.381-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2400	admin	2014-08-08 12:57:48.811-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2401	admin	2014-08-08 13:18:43.87-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2402	admin	2014-08-08 15:52:01.701-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2403	admin	2014-08-08 15:52:06.323-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2404	admin	2014-08-08 16:57:08.765-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2405	admin	2014-08-08 16:57:08.852-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2406	admin	2014-08-11 10:02:22.241-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2409	admin	2014-08-11 10:04:33.201-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2412	anonymousUser	2014-08-11 10:12:03.794-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2413	anonymousUser	2014-08-11 10:12:08.144-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2414	anonymousUser	2014-08-11 10:13:13.384-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2415	anonymousUser	2014-08-11 10:13:17.894-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2416	anonymousUser	2014-08-11 10:13:19.366-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2417	admin	2014-08-11 10:13:22.885-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2424	admin	2014-08-11 10:15:55.586-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2431	admin	2014-08-11 10:16:00.292-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2432	admin	2014-08-11 11:45:15.27-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2433	admin	2014-08-11 11:45:20.84-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2434	admin	2014-08-11 11:46:05.45-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2435	admin	2014-08-11 11:46:05.573-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2436	admin	2014-08-11 11:46:26.991-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2437	admin	2014-08-11 11:46:43.26-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2438	admin	2014-08-11 11:49:21.423-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2439	anonymousUser	2014-08-11 11:58:09-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2440	anonymousUser	2014-08-11 11:58:11.1-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2441	anonymousUser	2014-08-11 11:58:12.434-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2442	admin	2014-08-11 11:58:15.632-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2443	admin	2014-08-11 11:58:25.296-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2444	admin	2014-08-11 12:28:41.686-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2445	admin	2014-08-11 12:28:46.047-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2446	admin	2014-08-11 12:29:32.758-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2447	admin	2014-08-11 12:44:41.446-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2454	admin	2014-08-11 12:45:25.737-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2455	admin	2014-08-11 12:50:54.604-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2462	admin	2014-08-11 12:50:54.784-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2463	admin	2014-08-11 13:43:34.19-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2470	admin	2014-08-11 13:43:41.027-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2471	admin	2014-08-11 15:32:25.73-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2472	admin	2014-08-11 15:32:33.022-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2473	admin	2014-08-11 15:34:47.329-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2474	anonymousUser	2014-08-11 15:34:57.649-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2475	anonymousUser	2014-08-11 15:34:57.715-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2476	anonymousUser	2014-08-11 15:35:02.183-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2477	anonymousUser	2014-08-11 15:35:04.382-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2478	admin	2014-08-11 15:35:07.445-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2483	anonymousUser	2014-08-11 15:39:58.725-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2484	anonymousUser	2014-08-11 15:39:58.905-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2485	admin	2014-08-11 15:40:12.239-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2490	admin	2014-08-11 15:44:41.867-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2493	admin	2014-08-11 15:46:36.078-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2497	admin	2014-08-11 15:52:43.413-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2506	admin	2014-08-11 15:53:45.136-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2507	admin	2014-08-11 15:55:11.478-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2511	admin	2014-08-11 16:00:53.157-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2514	admin	2014-08-11 16:06:00.5-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2525	admin	2014-08-11 16:07:28.153-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2526	admin	2014-08-11 16:23:57.826-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2537	admin	2014-08-11 16:24:10.514-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2538	admin	2014-08-11 16:30:05.325-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2549	admin	2014-08-11 16:30:11.447-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2550	admin	2014-08-11 16:51:52.953-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2561	admin	2014-08-11 16:51:53.145-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2562	admin	2014-08-12 11:09:56.947-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2563	admin	2014-08-12 11:10:34.381-04	AFFILIATE_IMPORT_FAILURE	\N	\N	\N	\N	\N
2564	admin	2014-08-12 11:11:39.038-04	AFFILIATE_IMPORT_FAILURE	\N	\N	\N	\N	\N
2565	admin	2014-08-12 11:11:51.465-04	AFFILIATE_IMPORT_FAILURE	\N	\N	\N	\N	\N
2581	admin	2014-08-12 11:13:59.295-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2587	admin	2014-08-12 11:14:00.795-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2598	admin	2014-08-12 11:14:17.434-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2599	anonymousUser	2014-08-12 11:41:35.35-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2600	anonymousUser	2014-08-12 11:41:42.106-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2601	anonymousUser	2014-08-12 11:41:56.443-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2602	able939@mailinator.com	2014-08-12 14:18:22.353-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2603	anonymousUser	2014-08-12 14:36:31.67-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2604	anonymousUser	2014-08-12 14:36:32.704-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2605	admin	2014-08-12 14:36:36.159-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2606	admin	2014-08-12 14:57:19.999-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2633	admin	2014-08-12 15:09:23.618-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2634	anonymousUser	2014-08-12 15:10:06.362-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2635	anonymousUser	2014-08-12 15:10:12.597-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2636	anonymousUser	2014-08-12 15:10:21.812-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2637	anonymousUser	2014-08-12 15:10:25.83-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2638	able939@mailinator.com	2014-08-12 15:10:37.725-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2639	anonymousUser	2014-08-12 15:10:47.368-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2640	admin	2014-08-12 15:10:51.348-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2641	anonymousUser	2014-08-12 15:11:03.193-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2642	anonymousUser	2014-08-12 15:11:04.463-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2643	able949@mailinator.com	2014-08-12 15:11:15.879-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2644	anonymousUser	2014-08-12 15:11:24.017-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2645	anonymousUser	2014-08-12 15:11:25.16-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2646	admin	2014-08-12 15:11:28.26-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2647	admin	2014-08-12 15:11:39.735-04	APPLICATION_APPROVAL_STATE_CHANGED	2389	\N	\N	\N	\N
2648	anonymousUser	2014-08-12 15:11:41.225-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2649	anonymousUser	2014-08-12 15:11:42.574-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2650	able949@mailinator.com	2014-08-12 15:11:51.241-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2651	anonymousUser	2014-08-12 15:16:14.748-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2652	anonymousUser	2014-08-12 15:16:15.991-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2653	anonymousUser	2014-08-12 15:18:37.606-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2658	anonymousUser	2014-08-12 15:19:04.888-04	AFFILIATE_CREATED	\N	2654	\N	\N	\N
2659	anonymousUser	2014-08-12 15:19:04.989-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2660	anonymousUser	2014-08-12 15:19:18.392-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2664	able951@mailinator.com	2014-08-12 15:20:01.569-04	APPLICATION_APPROVAL_STATE_CHANGED	2656	\N	\N	\N	\N
2665	anonymousUser	2014-08-12 15:20:07.233-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2666	anonymousUser	2014-08-12 15:20:08.375-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2667	sweden	2014-08-12 15:20:13.653-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2668	sweden	2014-08-12 15:20:13.679-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2670	sweden	2014-08-12 15:20:36.478-04	APPLICATION_APPROVAL_STATE_CHANGED	2656	\N	\N	\N	\N
2671	sweden	2014-08-12 15:20:43.353-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2672	anonymousUser	2014-08-12 15:20:43.397-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2673	anonymousUser	2014-08-12 15:20:43.446-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2674	able951@mailinator.com	2014-08-12 15:20:53.274-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2675	able951@mailinator.com	2014-08-12 15:59:35.039-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2676	able951@mailinator.com	2014-08-12 16:20:03.953-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2677	staff	2014-08-12 16:20:14.74-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2678	staff	2014-08-12 16:20:14.774-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2679	staff	2014-08-12 16:20:26.797-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2680	anonymousUser	2014-08-12 16:20:26.849-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2681	anonymousUser	2014-08-12 16:20:26.922-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2682	admin	2014-08-12 16:20:30.906-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2683	admin	2014-08-12 17:06:14.675-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2684	admin	2014-08-12 17:06:14.675-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2685	anonymousUser	2014-08-13 10:52:29.931-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2686	anonymousUser	2014-08-13 10:52:30.596-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2687	anonymousUser	2014-08-13 10:52:30.636-04	AUTHORIZATION_FAILURE	\N	\N	\N	\N	\N
2688	admin	2014-08-13 10:52:35.82-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2689	admin	2014-08-13 10:59:03.599-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2690	admin	2014-08-13 10:59:41.661-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2701	admin	2014-08-13 12:29:11.51-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2712	admin	2014-08-13 12:30:05.065-04	AFFILIATE_IMPORT_SUCCESS	\N	\N	\N	\N	\N
2713	admin	2014-08-13 12:36:28.121-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2724	admin	2014-08-13 12:36:40.423-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2725	admin	2014-08-13 12:38:38.363-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2736	admin	2014-08-13 12:38:46.079-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2737	admin	2014-08-13 12:39:42.299-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2738	admin	2014-08-13 12:58:11.99-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2739	admin	2014-08-13 14:50:16.003-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2740	admin	2014-08-13 14:55:51.984-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2741	admin	2014-08-13 15:05:13.795-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2752	admin	2014-08-13 15:05:29.254-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2753	admin	2014-08-13 16:23:51.143-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2764	admin	2014-08-13 16:24:32.214-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2765	admin	2014-08-13 16:26:26.297-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2766	admin	2014-08-13 16:26:26.636-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2767	admin	2014-08-13 16:27:28.031-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2768	admin	2014-08-13 16:27:28.35-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2769	admin	2014-08-13 16:28:27.025-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2770	admin	2014-08-13 16:28:27.347-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2771	admin	2014-08-13 16:35:43.049-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2772	admin	2014-08-13 16:35:47.147-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2773	admin	2014-08-13 16:52:06.02-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2774	admin	2014-08-14 10:14:46.892-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2775	admin	2014-08-14 10:20:02.076-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2776	admin	2014-08-14 10:25:49.971-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2777	admin	2014-08-14 10:28:15.814-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2778	admin	2014-08-14 10:46:23.992-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2779	admin	2014-08-14 10:48:08.771-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2780	admin	2014-08-14 10:48:58.885-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2781	admin	2014-08-14 11:03:10.925-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2782	admin	2014-08-14 11:04:35.405-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2793	admin	2014-08-14 11:04:47.787-04	AFFILIATE_IMPORT	\N	\N	\N	\N	\N
2804	admin	2014-08-14 11:44:51.066-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2805	admin	2014-08-14 11:45:25.202-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2806	admin	2014-08-14 11:46:13.318-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2807	admin	2014-08-14 11:46:45.739-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2808	admin	2014-08-14 11:47:01.91-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
2809	admin	2014-08-14 13:38:40.497-04	AUTHENTICATION_SUCCESS	\N	\N	\N	\N	\N
2810	admin	2014-08-14 13:38:43.837-04	AFFILIATE_EXPORT	\N	\N	\N	\N	\N
\.


--
-- Data for Name: t_persistent_audit_event_data; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_persistent_audit_event_data (event_id, name, value) FROM stdin;
1	message	Access is denied
1	type	org.springframework.security.access.AccessDeniedException
2	message	Access is denied
2	type	org.springframework.security.access.AccessDeniedException
3	message	Access is denied
3	type	org.springframework.security.access.AccessDeniedException
4	message	Access is denied
4	type	org.springframework.security.access.AccessDeniedException
5	message	Access is denied
5	type	org.springframework.security.access.AccessDeniedException
6	sessionId	07E2CDBA00214CE308100C23A31C6D87
6	remoteAddress	127.0.0.1
7	message	Access is denied
7	type	org.springframework.security.access.AccessDeniedException
8	message	Access is denied
8	type	org.springframework.security.access.AccessDeniedException
9	remoteAddress	127.0.0.1
11	message	Access is denied
11	type	org.springframework.security.access.AccessDeniedException
12	message	Access is denied
12	type	org.springframework.security.access.AccessDeniedException
13	remoteAddress	127.0.0.1
14	message	Access is denied
14	type	org.springframework.security.access.AccessDeniedException
15	message	Access is denied
15	type	org.springframework.security.access.AccessDeniedException
16	remoteAddress	127.0.0.1
17	remoteAddress	127.0.0.1
18	remoteAddress	127.0.0.1
19	remoteAddress	127.0.0.1
22	remoteAddress	127.0.0.1
27	remoteAddress	127.0.0.1
36	remoteAddress	127.0.0.1
63	remoteAddress	127.0.0.1
67	remoteAddress	127.0.0.1
69	remoteAddress	127.0.0.1
71	remoteAddress	127.0.0.1
72	remoteAddress	127.0.0.1
73	remoteAddress	127.0.0.1
74	remoteAddress	127.0.0.1
78	remoteAddress	127.0.0.1
83	message	Access is denied
83	type	org.springframework.security.access.AccessDeniedException
84	message	Access is denied
84	type	org.springframework.security.access.AccessDeniedException
85	remoteAddress	127.0.0.1
86	message	Access is denied
86	type	org.springframework.security.access.AccessDeniedException
87	message	Access is denied
87	type	org.springframework.security.access.AccessDeniedException
88	remoteAddress	127.0.0.1
89	remoteAddress	127.0.0.1
90	remoteAddress	127.0.0.1
91	remoteAddress	127.0.0.1
92	remoteAddress	127.0.0.1
93	remoteAddress	127.0.0.1
94	remoteAddress	127.0.0.1
95	message	Access is denied
95	type	org.springframework.security.access.AccessDeniedException
96	message	Access is denied
96	type	org.springframework.security.access.AccessDeniedException
97	sessionId	8930B9B7CE46CBCC364243F722974C2A
97	remoteAddress	127.0.0.1
98	message	Access is denied
98	type	org.springframework.security.access.AccessDeniedException
99	message	Access is denied
99	type	org.springframework.security.access.AccessDeniedException
100	message	Access is denied
100	type	org.springframework.security.access.AccessDeniedException
101	message	Access is denied
101	type	org.springframework.security.access.AccessDeniedException
102	message	Access is denied
102	type	org.springframework.security.access.AccessDeniedException
103	message	Access is denied
103	type	org.springframework.security.access.AccessDeniedException
104	message	Access is denied
104	type	org.springframework.security.access.AccessDeniedException
105	message	Access is denied
105	type	org.springframework.security.access.AccessDeniedException
106	message	Access is denied
106	type	org.springframework.security.access.AccessDeniedException
107	message	Access is denied
107	type	org.springframework.security.access.AccessDeniedException
108	message	Access is denied
108	type	org.springframework.security.access.AccessDeniedException
109	sessionId	1CD9D352509FA480B03FE4991FFBD4FC
109	remoteAddress	127.0.0.1
112	remoteAddress	127.0.0.1
113	remoteAddress	127.0.0.1
114	message	Access is denied
114	type	org.springframework.security.access.AccessDeniedException
115	message	Access is denied
115	type	org.springframework.security.access.AccessDeniedException
116	message	Access is denied
116	type	org.springframework.security.access.AccessDeniedException
117	remoteAddress	127.0.0.1
120	remoteAddress	127.0.0.1
121	remoteAddress	127.0.0.1
122	remoteAddress	127.0.0.1
123	remoteAddress	127.0.0.1
124	remoteAddress	127.0.0.1
131	remoteAddress	127.0.0.1
132	message	Access is denied
132	type	org.springframework.security.access.AccessDeniedException
133	message	Access is denied
133	type	org.springframework.security.access.AccessDeniedException
134	message	Access is denied
135	message	Access is denied
134	type	org.springframework.security.access.AccessDeniedException
135	type	org.springframework.security.access.AccessDeniedException
136	remoteAddress	127.0.0.1
138	remoteAddress	127.0.0.1
143	remoteAddress	127.0.0.1
146	remoteAddress	127.0.0.1
147	remoteAddress	127.0.0.1
149	remoteAddress	127.0.0.1
150	remoteAddress	127.0.0.1
151	remoteAddress	127.0.0.1
152	remoteAddress	127.0.0.1
157	remoteAddress	127.0.0.1
168	remoteAddress	127.0.0.1
169	remoteAddress	127.0.0.1
171	remoteAddress	127.0.0.1
172	remoteAddress	127.0.0.1
173	remoteAddress	127.0.0.1
174	remoteAddress	127.0.0.1
175	remoteAddress	127.0.0.1
177	remoteAddress	127.0.0.1
187	remoteAddress	127.0.0.1
191	remoteAddress	127.0.0.1
194	remoteAddress	127.0.0.1
195	remoteAddress	127.0.0.1
196	remoteAddress	127.0.0.1
197	remoteAddress	127.0.0.1
198	remoteAddress	127.0.0.1
200	remoteAddress	127.0.0.1
204	remoteAddress	127.0.0.1
208	remoteAddress	127.0.0.1
209	remoteAddress	127.0.0.1
210	remoteAddress	127.0.0.1
211	remoteAddress	127.0.0.1
215	remoteAddress	127.0.0.1
216	remoteAddress	127.0.0.1
217	remoteAddress	127.0.0.1
218	remoteAddress	127.0.0.1
219	remoteAddress	127.0.0.1
224	remoteAddress	127.0.0.1
248	remoteAddress	127.0.0.1
251	remoteAddress	127.0.0.1
256	remoteAddress	127.0.0.1
260	message	Access is denied
260	type	org.springframework.security.access.AccessDeniedException
261	message	Access is denied
261	type	org.springframework.security.access.AccessDeniedException
262	remoteAddress	127.0.0.1
263	message	Access is denied
263	type	org.springframework.security.access.AccessDeniedException
264	message	Access is denied
264	type	org.springframework.security.access.AccessDeniedException
265	remoteAddress	127.0.0.1
270	remoteAddress	127.0.0.1
273	remoteAddress	127.0.0.1
277	remoteAddress	127.0.0.1
286	remoteAddress	127.0.0.1
287	remoteAddress	127.0.0.1
292	remoteAddress	127.0.0.1
293	remoteAddress	127.0.0.1
294	message	Access is denied
294	type	org.springframework.security.access.AccessDeniedException
295	message	Access is denied
295	type	org.springframework.security.access.AccessDeniedException
296	message	Access is denied
296	type	org.springframework.security.access.AccessDeniedException
297	message	Access is denied
297	type	org.springframework.security.access.AccessDeniedException
298	message	Access is denied
298	type	org.springframework.security.access.AccessDeniedException
299	message	Access is denied
299	type	org.springframework.security.access.AccessDeniedException
300	message	Access is denied
300	type	org.springframework.security.access.AccessDeniedException
301	message	Access is denied
301	type	org.springframework.security.access.AccessDeniedException
302	message	Access is denied
302	type	org.springframework.security.access.AccessDeniedException
303	message	Access is denied
303	type	org.springframework.security.access.AccessDeniedException
304	sessionId	F604F23A5B7ED4B82474E43CC6B5E9C2
304	remoteAddress	127.0.0.1
309	remoteAddress	127.0.0.1
339	remoteAddress	127.0.0.1
350	remoteAddress	127.0.0.1
648	remoteAddress	127.0.0.1
650	message	Access is denied
650	type	org.springframework.security.access.AccessDeniedException
651	message	Access is denied
651	type	org.springframework.security.access.AccessDeniedException
652	sessionId	CDD7B20A189FEE159FC2FA1C87DE99B2
652	remoteAddress	192.168.0.42
674	remoteAddress	127.0.0.1
686	remoteAddress	127.0.0.1
687	remoteAddress	127.0.0.1
690	remoteAddress	127.0.0.1
691	remoteAddress	127.0.0.1
692	remoteAddress	127.0.0.1
702	message	Access is denied
702	type	org.springframework.security.access.AccessDeniedException
706	message	Access is denied
706	type	org.springframework.security.access.AccessDeniedException
707	message	Access is denied
707	type	org.springframework.security.access.AccessDeniedException
709	message	Access is denied
709	type	org.springframework.security.access.AccessDeniedException
710	message	Access is denied
710	type	org.springframework.security.access.AccessDeniedException
711	message	Access is denied
711	type	org.springframework.security.access.AccessDeniedException
712	message	Access is denied
712	type	org.springframework.security.access.AccessDeniedException
713	message	Access is denied
713	type	org.springframework.security.access.AccessDeniedException
714	message	Access is denied
714	type	org.springframework.security.access.AccessDeniedException
715	message	Access is denied
715	type	org.springframework.security.access.AccessDeniedException
716	message	Access is denied
716	type	org.springframework.security.access.AccessDeniedException
717	message	Access is denied
717	type	org.springframework.security.access.AccessDeniedException
718	message	Access is denied
718	type	org.springframework.security.access.AccessDeniedException
719	message	Access is denied
719	type	org.springframework.security.access.AccessDeniedException
720	message	Access is denied
720	type	org.springframework.security.access.AccessDeniedException
721	message	Access is denied
721	type	org.springframework.security.access.AccessDeniedException
722	message	Access is denied
722	type	org.springframework.security.access.AccessDeniedException
723	message	Access is denied
723	type	org.springframework.security.access.AccessDeniedException
724	message	Access is denied
724	type	org.springframework.security.access.AccessDeniedException
725	message	Access is denied
725	type	org.springframework.security.access.AccessDeniedException
726	message	Access is denied
726	type	org.springframework.security.access.AccessDeniedException
727	message	Access is denied
727	type	org.springframework.security.access.AccessDeniedException
728	message	Access is denied
728	type	org.springframework.security.access.AccessDeniedException
729	message	Access is denied
729	type	org.springframework.security.access.AccessDeniedException
730	message	Access is denied
730	type	org.springframework.security.access.AccessDeniedException
731	message	Access is denied
731	type	org.springframework.security.access.AccessDeniedException
735	message	Access is denied
735	type	org.springframework.security.access.AccessDeniedException
736	message	Access is denied
736	type	org.springframework.security.access.AccessDeniedException
737	message	Access is denied
737	type	org.springframework.security.access.AccessDeniedException
738	message	Access is denied
738	type	org.springframework.security.access.AccessDeniedException
739	message	Access is denied
739	type	org.springframework.security.access.AccessDeniedException
740	message	Access is denied
740	type	org.springframework.security.access.AccessDeniedException
741	message	Access is denied
741	type	org.springframework.security.access.AccessDeniedException
742	message	Access is denied
742	type	org.springframework.security.access.AccessDeniedException
743	message	Access is denied
743	type	org.springframework.security.access.AccessDeniedException
744	message	Access is denied
744	type	org.springframework.security.access.AccessDeniedException
745	message	Access is denied
745	type	org.springframework.security.access.AccessDeniedException
746	message	Access is denied
746	type	org.springframework.security.access.AccessDeniedException
747	message	Access is denied
747	type	org.springframework.security.access.AccessDeniedException
748	message	Access is denied
748	type	org.springframework.security.access.AccessDeniedException
749	message	Access is denied
749	type	org.springframework.security.access.AccessDeniedException
750	message	Access is denied
750	type	org.springframework.security.access.AccessDeniedException
751	message	Access is denied
751	type	org.springframework.security.access.AccessDeniedException
752	message	Access is denied
752	type	org.springframework.security.access.AccessDeniedException
753	message	Access is denied
753	type	org.springframework.security.access.AccessDeniedException
754	message	Access is denied
754	type	org.springframework.security.access.AccessDeniedException
755	message	Access is denied
755	type	org.springframework.security.access.AccessDeniedException
756	message	Access is denied
756	type	org.springframework.security.access.AccessDeniedException
757	message	Access is denied
757	type	org.springframework.security.access.AccessDeniedException
758	message	Access is denied
758	type	org.springframework.security.access.AccessDeniedException
759	message	Access is denied
759	type	org.springframework.security.access.AccessDeniedException
760	message	Access is denied
760	type	org.springframework.security.access.AccessDeniedException
761	sessionId	2ACCE4909E2CF0D92DACC5D72A6715F0
761	remoteAddress	127.0.0.1
762	remoteAddress	127.0.0.1
763	remoteAddress	127.0.0.1
1002	message	Access is denied
1002	type	org.springframework.security.access.AccessDeniedException
1003	message	Access is denied
1003	type	org.springframework.security.access.AccessDeniedException
1004	sessionId	42A2FAE9E946F61C1494AB6100377041
1004	remoteAddress	192.168.0.42
1017	remoteAddress	127.0.0.1
1022	message	Access is denied
1022	type	org.springframework.security.access.AccessDeniedException
1023	message	Access is denied
1023	type	org.springframework.security.access.AccessDeniedException
1024	message	Access is denied
1024	type	org.springframework.security.access.AccessDeniedException
1027	remoteAddress	127.0.0.1
1028	remoteAddress	127.0.0.1
1029	message	Access is denied
1029	type	org.springframework.security.access.AccessDeniedException
1030	message	Access is denied
1030	type	org.springframework.security.access.AccessDeniedException
1031	remoteAddress	127.0.0.1
1032	remoteAddress	127.0.0.1
1033	remoteAddress	127.0.0.1
1034	remoteAddress	127.0.0.1
1035	remoteAddress	127.0.0.1
1036	remoteAddress	127.0.0.1
1037	remoteAddress	127.0.0.1
1038	message	Access is denied
1038	type	org.springframework.security.access.AccessDeniedException
1039	message	Access is denied
1039	type	org.springframework.security.access.AccessDeniedException
1040	remoteAddress	127.0.0.1
1041	message	Access is denied
1041	type	org.springframework.security.access.AccessDeniedException
1042	message	Access is denied
1042	type	org.springframework.security.access.AccessDeniedException
1043	remoteAddress	127.0.0.1
1044	remoteAddress	127.0.0.1
1051	remoteAddress	127.0.0.1
1053	message	Access is denied
1053	type	org.springframework.security.access.AccessDeniedException
1054	message	Access is denied
1054	type	org.springframework.security.access.AccessDeniedException
1055	remoteAddress	127.0.0.1
1056	remoteAddress	127.0.0.1
1057	remoteAddress	127.0.0.1
1058	remoteAddress	127.0.0.1
1059	message	Access is denied
1059	type	org.springframework.security.access.AccessDeniedException
1060	message	Access is denied
1060	type	org.springframework.security.access.AccessDeniedException
1061	sessionId	965558AA6D9E2CD67E731A82DE4259D5
1061	remoteAddress	127.0.0.1
1062	remoteAddress	127.0.0.1
1064	releasePackage.releasePackageId	1063
1064	releasePackage.name	Test Package
1065	remoteAddress	127.0.0.1
1066	remoteAddress	127.0.0.1
1068	releasePackage.releasePackageId	1067
1068	releasePackage.name	more
1069	remoteAddress	127.0.0.1
1070	remoteAddress	127.0.0.1
1071	remoteAddress	127.0.0.1
1072	remoteAddress	127.0.0.1
1074	releasePackage.releasePackageId	1073
1074	releasePackage.name	new 1
1075	remoteAddress	127.0.0.1
1076	remoteAddress	127.0.0.1
1077	remoteAddress	127.0.0.1
1078	releasePackage.releasePackageId	1047
1078	releasePackage.name	aoeu111
1079	remoteAddress	127.0.0.1
1080	remoteAddress	127.0.0.1
1081	remoteAddress	127.0.0.1
1082	remoteAddress	127.0.0.1
1083	remoteAddress	127.0.0.1
1084	message	Access is denied
1084	type	org.springframework.security.access.AccessDeniedException
1085	message	Access is denied
1085	type	org.springframework.security.access.AccessDeniedException
1086	message	Access is denied
1086	type	org.springframework.security.access.AccessDeniedException
1087	message	Access is denied
1087	type	org.springframework.security.access.AccessDeniedException
1088	message	Access is denied
1088	type	org.springframework.security.access.AccessDeniedException
1089	sessionId	B7D1B146CB88638A08DC705DBFA357A2
1089	remoteAddress	127.0.0.1
1090	remoteAddress	127.0.0.1
1091	message	Access is denied
1091	type	org.springframework.security.access.AccessDeniedException
1092	message	Access is denied
1092	type	org.springframework.security.access.AccessDeniedException
1093	sessionId	9584C60834D9C18A91A4F946D3D5AF50
1093	remoteAddress	127.0.0.1
1100	remoteAddress	127.0.0.1
1101	remoteAddress	127.0.0.1
1102	remoteAddress	127.0.0.1
1103	remoteAddress	127.0.0.1
1104	remoteAddress	127.0.0.1
1105	remoteAddress	127.0.0.1
1106	remoteAddress	127.0.0.1
1107	remoteAddress	127.0.0.1
1108	remoteAddress	127.0.0.1
1109	remoteAddress	127.0.0.1
1110	remoteAddress	127.0.0.1
1111	remoteAddress	127.0.0.1
1112	remoteAddress	127.0.0.1
1113	remoteAddress	127.0.0.1
1114	remoteAddress	127.0.0.1
1119	remoteAddress	127.0.0.1
1120	remoteAddress	127.0.0.1
1121	message	Access is denied
1121	type	org.springframework.security.access.AccessDeniedException
1122	message	Access is denied
1122	type	org.springframework.security.access.AccessDeniedException
1123	message	Access is denied
1123	type	org.springframework.security.access.AccessDeniedException
1124	message	Access is denied
1124	type	org.springframework.security.access.AccessDeniedException
1125	message	Access is denied
1125	type	org.springframework.security.access.AccessDeniedException
1126	sessionId	6704D09E9EA8C5F4935F349BC3CE59A6
1126	remoteAddress	127.0.0.1
1127	message	Access is denied
1127	type	org.springframework.security.access.AccessDeniedException
1128	message	Access is denied
1128	type	org.springframework.security.access.AccessDeniedException
1129	message	Access is denied
1129	type	org.springframework.security.access.AccessDeniedException
1130	remoteAddress	127.0.0.1
1131	remoteAddress	127.0.0.1
1132	remoteAddress	127.0.0.1
1133	remoteAddress	127.0.0.1
1134	remoteAddress	127.0.0.1
1135	remoteAddress	127.0.0.1
1136	remoteAddress	127.0.0.1
1137	application.applicationId	1018
1137	application.approvalState	SUBMITTED
1139	releaseVersion.releaseVersionId	1138
1139	releaseVersion.name	version 1
1141	releaseFile.releaseFileId	1140
1141	releaseFile.label	Hello
1142	remoteAddress	127.0.0.1
1143	application.applicationId	1018
1143	application.approvalState	CHANGE_REQUESTED
1143	application.name	923 organazitaion
1144	remoteAddress	127.0.0.1
1145	application.applicationId	1018
1145	application.approvalState	RESUBMITTED
1145	application.name	923 organazitaion
1146	application.applicationId	1018
1146	application.approvalState	APPROVED
1146	application.name	923 organazitaion
1147	application.applicationId	1018
1147	application.approvalState	SUBMITTED
1147	application.name	923 organazitaion
1148	application.applicationId	1018
1148	application.approvalState	CHANGE_REQUESTED
1148	application.name	923 organazitaion
1149	application.applicationId	1018
1149	application.approvalState	RESUBMITTED
1149	application.name	923 organazitaion
1150	application.applicationId	1018
1150	application.approvalState	CHANGE_REQUESTED
1150	application.name	923 organazitaion
1151	application.applicationId	1018
1151	application.approvalState	RESUBMITTED
1151	application.name	923 organazitaion
1152	application.applicationId	1018
1152	application.approvalState	APPROVED
1152	application.name	923 organazitaion
1153	application.applicationId	1018
1153	application.approvalState	SUBMITTED
1153	application.name	923 organazitaion
1154	sessionId	3D2D172DBF84AF5BA62E2C7C6E819D8A
1154	remoteAddress	127.0.0.1
1155	sessionId	02E9D68562157E59970F5C2CBE8759A4
1155	remoteAddress	127.0.0.1
1156	message	Access is denied
1156	type	org.springframework.security.access.AccessDeniedException
1157	message	Access is denied
1157	type	org.springframework.security.access.AccessDeniedException
1158	message	Access is denied
1158	type	org.springframework.security.access.AccessDeniedException
1159	sessionId	C66259191644EC40D789FCC9F94C03C6
1159	remoteAddress	127.0.0.1
1160	remoteAddress	127.0.0.1
1161	remoteAddress	127.0.0.1
1162	message	Access is denied
1162	type	org.springframework.security.access.AccessDeniedException
1166	message	Access is denied
1166	type	org.springframework.security.access.AccessDeniedException
1167	message	Access is denied
1167	type	org.springframework.security.access.AccessDeniedException
1172	application.applicationId	1163
1172	application.approvalState	SUBMITTED
1172	application.name	able925 org
1173	remoteAddress	127.0.0.1
1174	remoteAddress	127.0.0.1
1175	application.applicationId	1163
1175	application.approvalState	CHANGE_REQUESTED
1175	application.name	able925 org
1176	message	Access is denied
1176	type	org.springframework.security.access.AccessDeniedException
1177	message	Access is denied
1177	type	org.springframework.security.access.AccessDeniedException
1178	message	Access is denied
1178	type	org.springframework.security.access.AccessDeniedException
1179	sessionId	49B0F8618033E83A11F0B14F020FCB71
1179	remoteAddress	127.0.0.1
1180	remoteAddress	127.0.0.1
1181	remoteAddress	127.0.0.1
1182	remoteAddress	127.0.0.1
1183	remoteAddress	127.0.0.1
1184	remoteAddress	127.0.0.1
1185	remoteAddress	127.0.0.1
1186	application.applicationId	1163
1186	application.approvalState	APPROVED
1186	application.name	able925 org
1187	remoteAddress	127.0.0.1
1188	application.applicationId	1163
1188	application.approvalState	APPROVED
1188	application.name	able925 org
1189	remoteAddress	127.0.0.1
1190	remoteAddress	127.0.0.1
1191	application.applicationId	1163
1191	application.approvalState	SUBMITTED
1191	application.name	able925 org
1192	remoteAddress	127.0.0.1
1193	application.applicationId	1163
1193	application.approvalState	CHANGE_REQUESTED
1193	application.name	able925 org
1194	application.applicationId	1163
1194	application.approvalState	SUBMITTED
1194	application.name	able925 org
1195	application.applicationId	1163
1195	application.approvalState	SUBMITTED
1195	application.name	able925 org
1196	application.applicationId	1163
1196	application.approvalState	SUBMITTED
1196	application.name	able925 org
1197	application.applicationId	1163
1198	application.applicationId	1163
1197	application.approvalState	APPROVED
1197	application.name	able925 org
1198	application.approvalState	SUBMITTED
1198	application.name	able925 org
1199	message	Access is denied
1199	type	org.springframework.security.access.AccessDeniedException
1203	message	Access is denied
1203	type	org.springframework.security.access.AccessDeniedException
1204	message	Access is denied
1204	type	org.springframework.security.access.AccessDeniedException
1206	application.applicationId	1200
1206	application.approvalState	SUBMITTED
1206	application.name	able 926 ORG
1207	message	Access is denied
1207	type	org.springframework.security.access.AccessDeniedException
1211	message	Access is denied
1211	type	org.springframework.security.access.AccessDeniedException
1212	message	Access is denied
1212	type	org.springframework.security.access.AccessDeniedException
1213	message	Access is denied
1213	type	org.springframework.security.access.AccessDeniedException
1214	message	Access is denied
1214	type	org.springframework.security.access.AccessDeniedException
1215	message	Access is denied
1215	type	org.springframework.security.access.AccessDeniedException
1216	remoteAddress	127.0.0.1
1217	application.applicationId	1200
1217	application.approvalState	APPROVED
1217	application.name	able 926 ORG
1218	remoteAddress	127.0.0.1
1219	remoteAddress	127.0.0.1
1220	message	Access is denied
1220	type	org.springframework.security.access.AccessDeniedException
1221	message	Access is denied
1221	type	org.springframework.security.access.AccessDeniedException
1222	message	Access is denied
1222	type	org.springframework.security.access.AccessDeniedException
1223	message	Access is denied
1223	type	org.springframework.security.access.AccessDeniedException
1224	sessionId	9780B5885DB724EE9DD57AA23E6B0B2D
1224	remoteAddress	127.0.0.1
1225	application.applicationId	1200
1225	application.approvalState	SUBMITTED
1225	application.name	able 926 ORG
1226	remoteAddress	127.0.0.1
1227	application.applicationId	1200
1227	application.approvalState	APPROVED
1227	application.name	able 926 ORG
1228	remoteAddress	127.0.0.1
1229	message	Access is denied
1229	type	org.springframework.security.access.AccessDeniedException
1230	remoteAddress	127.0.0.1
1231	message	Access is denied
1231	type	org.springframework.security.access.AccessDeniedException
1232	message	Access is denied
1232	type	org.springframework.security.access.AccessDeniedException
1233	message	Access is denied
1233	type	org.springframework.security.access.AccessDeniedException
1234	remoteAddress	127.0.0.1
1235	sessionId	916D87F74F294A596D63E7CA7D005BDA
1235	remoteAddress	127.0.0.1
1236	message	Access is denied
1236	type	org.springframework.security.access.AccessDeniedException
1237	message	Access is denied
1237	type	org.springframework.security.access.AccessDeniedException
1238	remoteAddress	127.0.0.1
1239	message	Access is denied
1239	type	org.springframework.security.access.AccessDeniedException
1240	message	Access is denied
1240	type	org.springframework.security.access.AccessDeniedException
1241	remoteAddress	127.0.0.1
1242	remoteAddress	127.0.0.1
1243	message	Access is denied
1243	type	org.springframework.security.access.AccessDeniedException
1244	message	Access is denied
1244	type	org.springframework.security.access.AccessDeniedException
1245	sessionId	16CC0E342EA84C8E2424D0BAE67AE0B4
1245	remoteAddress	127.0.0.1
1246	message	Access is denied
1246	type	org.springframework.security.access.AccessDeniedException
1247	message	Access is denied
1247	type	org.springframework.security.access.AccessDeniedException
1248	message	Access is denied
1248	type	org.springframework.security.access.AccessDeniedException
1249	message	Access is denied
1249	type	org.springframework.security.access.AccessDeniedException
1250	message	Access is denied
1250	type	org.springframework.security.access.AccessDeniedException
1251	message	Access is denied
1251	type	org.springframework.security.access.AccessDeniedException
1252	message	Access is denied
1252	type	org.springframework.security.access.AccessDeniedException
1253	message	Access is denied
1253	type	org.springframework.security.access.AccessDeniedException
1257	message	Access is denied
1257	type	org.springframework.security.access.AccessDeniedException
1258	message	Access is denied
1258	type	org.springframework.security.access.AccessDeniedException
1259	message	Access is denied
1259	type	org.springframework.security.access.AccessDeniedException
1260	message	Access is denied
1260	type	org.springframework.security.access.AccessDeniedException
1261	remoteAddress	127.0.0.1
1262	message	Access is denied
1262	type	org.springframework.security.access.AccessDeniedException
1266	message	Access is denied
1266	type	org.springframework.security.access.AccessDeniedException
1267	message	Access is denied
1267	type	org.springframework.security.access.AccessDeniedException
1268	message	Access is denied
1268	type	org.springframework.security.access.AccessDeniedException
1272	message	Access is denied
1272	type	org.springframework.security.access.AccessDeniedException
1273	message	Access is denied
1273	type	org.springframework.security.access.AccessDeniedException
1274	message	Access is denied
1274	type	org.springframework.security.access.AccessDeniedException
1275	message	Access is denied
1275	type	org.springframework.security.access.AccessDeniedException
1280	message	Access is denied
1280	type	org.springframework.security.access.AccessDeniedException
1281	message	Access is denied
1281	type	org.springframework.security.access.AccessDeniedException
1282	message	Access is denied
1282	type	org.springframework.security.access.AccessDeniedException
1283	message	Access is denied
1283	type	org.springframework.security.access.AccessDeniedException
1284	message	Access is denied
1284	type	org.springframework.security.access.AccessDeniedException
1285	sessionId	F0F8E4BA1E4B95FA20CA68B7F88727D2
1285	remoteAddress	127.0.0.1
1286	message	Access is denied
1286	type	org.springframework.security.access.AccessDeniedException
1287	message	Access is denied
1287	type	org.springframework.security.access.AccessDeniedException
1288	remoteAddress	127.0.0.1
1289	message	Access is denied
1289	type	org.springframework.security.access.AccessDeniedException
1290	message	Access is denied
1290	type	org.springframework.security.access.AccessDeniedException
1291	remoteAddress	127.0.0.1
1294	application.applicationId	1277
1294	application.approvalState	SUBMITTED
1294	application.name	some org
1295	message	Access is denied
1295	type	org.springframework.security.access.AccessDeniedException
1296	message	Access is denied
1296	type	org.springframework.security.access.AccessDeniedException
1297	remoteAddress	127.0.0.1
1298	remoteAddress	127.0.0.1
1299	message	Access is denied
1299	type	org.springframework.security.access.AccessDeniedException
1300	message	Access is denied
1300	type	org.springframework.security.access.AccessDeniedException
1301	message	Access is denied
1301	type	org.springframework.security.access.AccessDeniedException
1302	sessionId	0DEB6C95BC6FEDF3615F1698B6691F5A
1302	remoteAddress	127.0.0.1
1303	message	Access is denied
1303	type	org.springframework.security.access.AccessDeniedException
1307	message	Access is denied
1307	type	org.springframework.security.access.AccessDeniedException
1308	message	Access is denied
1308	type	org.springframework.security.access.AccessDeniedException
1309	message	Access is denied
1309	type	org.springframework.security.access.AccessDeniedException
1310	message	Access is denied
1310	type	org.springframework.security.access.AccessDeniedException
1311	message	Access is denied
1311	type	org.springframework.security.access.AccessDeniedException
1312	sessionId	B1EA5C70C2FD484F424CA4F70226F0B1
1312	remoteAddress	127.0.0.1
1313	message	Access is denied
1313	type	org.springframework.security.access.AccessDeniedException
1314	message	Access is denied
1314	type	org.springframework.security.access.AccessDeniedException
1315	remoteAddress	127.0.0.1
1316	application.applicationId	1277
1316	application.approvalState	CHANGE_REQUESTED
1316	application.name	some org
1317	message	Access is denied
1317	type	org.springframework.security.access.AccessDeniedException
1318	message	Access is denied
1318	type	org.springframework.security.access.AccessDeniedException
1319	remoteAddress	127.0.0.1
1320	remoteAddress	127.0.0.1
1321	remoteAddress	127.0.0.1
1322	remoteAddress	127.0.0.1
1325	remoteAddress	127.0.0.1
1326	application.applicationId	1277
1326	application.approvalState	RESUBMITTED
1326	application.name	931 Organization five again more
1327	message	Access is denied
1327	type	org.springframework.security.access.AccessDeniedException
1328	remoteAddress	127.0.0.1
1329	application.applicationId	1277
1329	application.approvalState	CHANGE_REQUESTED
1329	application.name	931 Organization five again more
1330	message	Access is denied
1330	type	org.springframework.security.access.AccessDeniedException
1331	remoteAddress	127.0.0.1
1332	application.applicationId	1277
1332	application.approvalState	RESUBMITTED
1332	application.name	931 Organization five again more2
1333	message	Access is denied
1333	type	org.springframework.security.access.AccessDeniedException
1334	message	Access is denied
1334	type	org.springframework.security.access.AccessDeniedException
1335	remoteAddress	127.0.0.1
1336	message	Access is denied
1336	type	org.springframework.security.access.AccessDeniedException
1340	message	Access is denied
1340	type	org.springframework.security.access.AccessDeniedException
1343	application.applicationId	1337
1343	application.approvalState	SUBMITTED
1343	application.name	932 Organization
1346	remoteAddress	127.0.0.1
1347	application.applicationId	1277
1347	application.approvalState	APPROVED
1347	application.name	931 Organization five again more2
1348	application.applicationId	1337
1348	application.approvalState	CHANGE_REQUESTED
1348	application.name	932 Organization
1349	message	Access is denied
1349	type	org.springframework.security.access.AccessDeniedException
1350	message	Access is denied
1350	type	org.springframework.security.access.AccessDeniedException
1351	remoteAddress	127.0.0.1
1354	application.applicationId	1337
1354	application.approvalState	RESUBMITTED
1354	application.name	932 Organization 2
1356	message	Access is denied
1356	type	org.springframework.security.access.AccessDeniedException
1359	message	Access is denied
1359	type	org.springframework.security.access.AccessDeniedException
1360	message	Access is denied
1360	type	org.springframework.security.access.AccessDeniedException
1361	remoteAddress	127.0.0.1
1341	message	Access is denied
1341	type	org.springframework.security.access.AccessDeniedException
1344	message	Access is denied
1344	type	org.springframework.security.access.AccessDeniedException
1345	message	Access is denied
1345	type	org.springframework.security.access.AccessDeniedException
1355	message	Access is denied
1355	type	org.springframework.security.access.AccessDeniedException
1357	remoteAddress	127.0.0.1
1358	application.applicationId	1337
1358	application.approvalState	REJECTED
1358	application.name	932 Organization 2
1362	application.applicationId	1337
1362	application.approvalState	SUBMITTED
1362	application.name	932 Organization 2222 2
1363	remoteAddress	127.0.0.1
1364	application.applicationId	1337
1364	application.approvalState	SUBMITTED
1364	application.name	932 Organization 2222 2
1365	application.applicationId	1337
1365	application.approvalState	RESUBMITTED
1365	application.name	932 Organization 2222 requested
1366	application.applicationId	1337
1366	application.approvalState	RESUBMITTED
1366	application.name	932 Organization 2222 requested ILLEGAL
1367	message	Access is denied
1367	type	org.springframework.security.access.AccessDeniedException
1368	message	Access is denied
1368	type	org.springframework.security.access.AccessDeniedException
1369	remoteAddress	127.0.0.1
1371	releaseVersion.releaseVersionId	1370
1371	releaseVersion.name	Version 2
1372	message	Access is denied
1372	type	org.springframework.security.access.AccessDeniedException
1373	message	Access is denied
1373	type	org.springframework.security.access.AccessDeniedException
1374	remoteAddress	127.0.0.1
1375	remoteAddress	127.0.0.1
1376	remoteAddress	127.0.0.1
1377	application.applicationId	1018
1377	application.approvalState	RESUBMITTED
1377	application.name	923 organazitaion
1378	message	Access is denied
1378	type	org.springframework.security.access.AccessDeniedException
1379	message	Access is denied
1379	type	org.springframework.security.access.AccessDeniedException
1380	remoteAddress	127.0.0.1
1381	application.applicationId	1018
1381	application.approvalState	APPROVED
1381	application.name	923 organazitaion
1382	message	Access is denied
1382	type	org.springframework.security.access.AccessDeniedException
1386	affiliate.creator	able933@mailinator.com
1387	message	Access is denied
1387	type	org.springframework.security.access.AccessDeniedException
1388	message	Access is denied
1388	type	org.springframework.security.access.AccessDeniedException
1389	application.applicationId	1383
1389	application.approvalState	SUBMITTED
1389	application.name	933 org
1390	message	Access is denied
1390	type	org.springframework.security.access.AccessDeniedException
1391	message	Access is denied
1391	type	org.springframework.security.access.AccessDeniedException
1392	remoteAddress	127.0.0.1
1393	remoteAddress	127.0.0.1
1395	releasePackage.name	My package
1397	releaseVersion.name	my version 1
1399	releaseFile.label	my file 1
1401	releaseFile.label	my file 2
1402	remoteAddress	127.0.0.1
1403	releaseVersion.name	my version 1
1405	releaseVersion.name	my version 2
1407	releaseFile.label	my file 2.1
1408	releaseVersion.name	my version 2
1409	releaseVersion.name	my version 2
1411	releaseVersion.name	my version 3
1413	releaseFile.label	my file 3.1
1414	remoteAddress	127.0.0.1
1415	releaseFile.label	my file 3.1
1416	remoteAddress	127.0.0.1
1418	releaseFile.label	my file 3.11
1420	releaseFile.label	to delete
1421	releaseFile.label	to delete
1422	releaseVersion.name	my version 3a
1423	releaseVersion.name	my version 3aa
1424	message	Access is denied
1424	type	org.springframework.security.access.AccessDeniedException
1428	affiliate.creator	able934@mailinator.com
1429	message	Access is denied
1429	type	org.springframework.security.access.AccessDeniedException
1430	message	Access is denied
1430	type	org.springframework.security.access.AccessDeniedException
1431	application.applicationId	1425
1431	application.approvalState	SUBMITTED
1431	application.name	111
1432	message	Access is denied
1432	type	org.springframework.security.access.AccessDeniedException
1433	message	Access is denied
1433	type	org.springframework.security.access.AccessDeniedException
1434	remoteAddress	127.0.0.1
1435	application.applicationId	1425
1435	application.approvalState	APPROVED
1435	application.name	111
1436	remoteAddress	127.0.0.1
1439	remoteAddress	127.0.0.1
1443	affiliate.creator	able935@mailinator.com
1448	application.applicationId	1440
1448	application.approvalState	SUBMITTED
1448	application.name	935 Organization
1449	message	Access is denied
1450	message	Access is denied
1451	message	Access is denied
1449	type	org.springframework.security.access.AccessDeniedException
1451	type	org.springframework.security.access.AccessDeniedException
1450	type	org.springframework.security.access.AccessDeniedException
1452	sessionId	7876161D930D522DB04C67F211662AAC
1452	remoteAddress	127.0.0.1
1453	message	Access is denied
1453	type	org.springframework.security.access.AccessDeniedException
1454	message	Access is denied
1454	type	org.springframework.security.access.AccessDeniedException
1455	remoteAddress	127.0.0.1
1456	application.applicationId	1440
1456	application.approvalState	CHANGE_REQUESTED
1456	application.name	935 Organization
1457	message	Access is denied
1457	type	org.springframework.security.access.AccessDeniedException
1458	message	Access is denied
1458	type	org.springframework.security.access.AccessDeniedException
1459	remoteAddress	127.0.0.1
1460	message	Access is denied
1460	type	org.springframework.security.access.AccessDeniedException
1461	message	Access is denied
1461	type	org.springframework.security.access.AccessDeniedException
1462	remoteAddress	127.0.0.1
1463	message	Access is denied
1463	type	org.springframework.security.access.AccessDeniedException
1464	message	Access is denied
1464	type	org.springframework.security.access.AccessDeniedException
1465	remoteAddress	127.0.0.1
1466	application.applicationId	1440
1466	application.approvalState	RESUBMITTED
1466	application.name	935 Organization
1467	message	Access is denied
1467	type	org.springframework.security.access.AccessDeniedException
1468	message	Access is denied
1468	type	org.springframework.security.access.AccessDeniedException
1469	remoteAddress	127.0.0.1
1470	application.applicationId	1440
1470	application.approvalState	APPROVED
1470	application.name	935 Organization
1471	message	Access is denied
1471	type	org.springframework.security.access.AccessDeniedException
1472	message	Access is denied
1472	type	org.springframework.security.access.AccessDeniedException
1473	remoteAddress	127.0.0.1
1474	remoteAddress	127.0.0.1
1475	message	Access is denied
1475	type	org.springframework.security.access.AccessDeniedException
1476	message	Access is denied
1476	type	org.springframework.security.access.AccessDeniedException
1477	remoteAddress	127.0.0.1
1478	message	Access is denied
1478	type	org.springframework.security.access.AccessDeniedException
1479	message	Access is denied
1479	type	org.springframework.security.access.AccessDeniedException
1480	remoteAddress	127.0.0.1
1481	message	An Authentication object was not found in the SecurityContext
1481	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
1482	message	Access is denied
1482	type	org.springframework.security.access.AccessDeniedException
1483	remoteAddress	127.0.0.1
1484	message	Access is denied
1484	type	org.springframework.security.access.AccessDeniedException
1485	message	Access is denied
1485	type	org.springframework.security.access.AccessDeniedException
1486	remoteAddress	127.0.0.1
1487	remoteAddress	127.0.0.1
1488	remoteAddress	127.0.0.1
1489	remoteAddress	127.0.0.1
1490	remoteAddress	127.0.0.1
1491	remoteAddress	127.0.0.1
1492	message	Access is denied
1492	type	org.springframework.security.access.AccessDeniedException
1497	affiliate.creator	able936@mailinator.com
1498	message	Access is denied
1498	type	org.springframework.security.access.AccessDeniedException
1499	message	Access is denied
1499	type	org.springframework.security.access.AccessDeniedException
1503	application.applicationId	1494
1503	application.approvalState	SUBMITTED
1503	application.name	936 org
1504	message	Access is denied
1504	type	org.springframework.security.access.AccessDeniedException
1505	message	Access is denied
1505	type	org.springframework.security.access.AccessDeniedException
1506	remoteAddress	127.0.0.1
1507	application.applicationId	1494
1507	application.approvalState	APPROVED
1507	application.name	936 org
1508	message	Access is denied
1508	type	org.springframework.security.access.AccessDeniedException
1509	message	Access is denied
1509	type	org.springframework.security.access.AccessDeniedException
1510	remoteAddress	127.0.0.1
1511	remoteAddress	127.0.0.1
1516	affiliate.creator	able937@mailinator.com
1517	message	Access is denied
1517	type	org.springframework.security.access.AccessDeniedException
1518	application.applicationId	1513
1518	application.approvalState	SUBMITTED
1518	application.name	937 Org
1520	message	Access is denied
1520	type	org.springframework.security.access.AccessDeniedException
1525	message	Access is denied
1525	type	org.springframework.security.access.AccessDeniedException
1526	remoteAddress	127.0.0.1
1527	message	Access is denied
1527	type	org.springframework.security.access.AccessDeniedException
1528	remoteAddress	127.0.0.1
1529	message	Access is denied
1529	type	org.springframework.security.access.AccessDeniedException
1530	message	Access is denied
1530	type	org.springframework.security.access.AccessDeniedException
1533	message	Access is denied
1533	type	org.springframework.security.access.AccessDeniedException
1534	message	Access is denied
1534	type	org.springframework.security.access.AccessDeniedException
1536	message	Access is denied
1536	type	org.springframework.security.access.AccessDeniedException
1537	message	Access is denied
1537	type	org.springframework.security.access.AccessDeniedException
1519	message	Access is denied
1519	type	org.springframework.security.access.AccessDeniedException
1521	remoteAddress	127.0.0.1
1523	application.applicationId	1513
1523	application.approvalState	APPROVED
1523	application.name	937 Org
1524	message	Access is denied
1524	type	org.springframework.security.access.AccessDeniedException
1531	message	Access is denied
1531	type	org.springframework.security.access.AccessDeniedException
1532	message	Access is denied
1532	type	org.springframework.security.access.AccessDeniedException
1535	message	Access is denied
1535	type	org.springframework.security.access.AccessDeniedException
1538	remoteAddress	127.0.0.1
1539	message	Access is denied
1539	type	org.springframework.security.access.AccessDeniedException
1540	remoteAddress	127.0.0.1
1541	message	Access is denied
1541	type	org.springframework.security.access.AccessDeniedException
1542	message	Access is denied
1542	type	org.springframework.security.access.AccessDeniedException
1543	sessionId	DCEFCE129A613B60E03F4C887607A71A
1543	remoteAddress	127.0.0.1
1544	remoteAddress	127.0.0.1
1545	remoteAddress	127.0.0.1
1546	message	Access is denied
1546	type	org.springframework.security.access.AccessDeniedException
1547	remoteAddress	127.0.0.1
1548	remoteAddress	127.0.0.1
1550	remoteAddress	127.0.0.1
1549	remoteAddress	127.0.0.1
1551	message	Access is denied
1551	type	org.springframework.security.access.AccessDeniedException
1552	message	Access is denied
1552	type	org.springframework.security.access.AccessDeniedException
1553	message	Access is denied
1553	type	org.springframework.security.access.AccessDeniedException
1554	sessionId	7A237745598B1BFD4B8EF804298F17D4
1554	remoteAddress	127.0.0.1
1559	message	Access is denied
1559	type	org.springframework.security.access.AccessDeniedException
1560	message	Access is denied
1560	type	org.springframework.security.access.AccessDeniedException
1561	remoteAddress	127.0.0.1
1562	remoteAddress	127.0.0.1
1563	remoteAddress	127.0.0.1
1564	message	Access is denied
1564	type	org.springframework.security.access.AccessDeniedException
1565	message	Access is denied
1565	type	org.springframework.security.access.AccessDeniedException
1566	remoteAddress	127.0.0.1
1567	remoteAddress	127.0.0.1
1568	message	An Authentication object was not found in the SecurityContext
1568	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
1573	affiliate.creator	able938@mailinator.com
1574	message	Access is denied
1574	type	org.springframework.security.access.AccessDeniedException
1575	message	Access is denied
1575	type	org.springframework.security.access.AccessDeniedException
1576	message	Access is denied
1576	type	org.springframework.security.access.AccessDeniedException
1577	message	Access is denied
1577	type	org.springframework.security.access.AccessDeniedException
1578	message	Access is denied
1578	type	org.springframework.security.access.AccessDeniedException
1579	message	Access is denied
1579	type	org.springframework.security.access.AccessDeniedException
1580	message	Access is denied
1580	type	org.springframework.security.access.AccessDeniedException
1581	sessionId	B017142343B5CD6052515DDE5967D991
1581	remoteAddress	127.0.0.1
1582	message	Access is denied
1582	type	org.springframework.security.access.AccessDeniedException
1583	remoteAddress	127.0.0.1
1588	application.applicationId	1570
1588	application.approvalState	SUBMITTED
1588	application.name	938 Org
1589	message	Access is denied
1589	type	org.springframework.security.access.AccessDeniedException
1590	message	Access is denied
1590	type	org.springframework.security.access.AccessDeniedException
1591	remoteAddress	127.0.0.1
1592	application.applicationId	1570
1592	application.approvalState	CHANGE_REQUESTED
1592	application.name	938 Org
1593	message	Access is denied
1593	type	org.springframework.security.access.AccessDeniedException
1594	message	Access is denied
1594	type	org.springframework.security.access.AccessDeniedException
1595	remoteAddress	127.0.0.1
1596	application.applicationId	1570
1596	application.approvalState	RESUBMITTED
1596	application.name	938 Org
1597	message	Access is denied
1597	type	org.springframework.security.access.AccessDeniedException
1598	remoteAddress	127.0.0.1
1599	message	Access is denied
1599	type	org.springframework.security.access.AccessDeniedException
1600	message	Access is denied
1600	type	org.springframework.security.access.AccessDeniedException
1601	remoteAddress	127.0.0.1
1602	message	An Authentication object was not found in the SecurityContext
1663	type	org.springframework.security.access.AccessDeniedException
1602	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
1603	message	Access is denied
1603	type	org.springframework.security.access.AccessDeniedException
1604	remoteAddress	127.0.0.1
1605	remoteAddress	127.0.0.1
1606	message	Access is denied
1606	type	org.springframework.security.access.AccessDeniedException
1607	message	Access is denied
1607	type	org.springframework.security.access.AccessDeniedException
1608	remoteAddress	127.0.0.1
1609	message	Access is denied
1609	type	org.springframework.security.access.AccessDeniedException
1610	remoteAddress	127.0.0.1
1611	message	Access is denied
1611	type	org.springframework.security.access.AccessDeniedException
1612	remoteAddress	127.0.0.1
1613	message	Access is denied
1613	type	org.springframework.security.access.AccessDeniedException
1614	message	Access is denied
1614	type	org.springframework.security.access.AccessDeniedException
1615	remoteAddress	127.0.0.1
1616	message	Access is denied
1616	type	org.springframework.security.access.AccessDeniedException
1617	message	Access is denied
1617	type	org.springframework.security.access.AccessDeniedException
1618	remoteAddress	127.0.0.1
1619	message	Access is denied
1619	type	org.springframework.security.access.AccessDeniedException
1620	message	Access is denied
1620	type	org.springframework.security.access.AccessDeniedException
1621	remoteAddress	127.0.0.1
1622	message	An Authentication object was not found in the SecurityContext
1622	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
1623	message	Access is denied
1623	type	org.springframework.security.access.AccessDeniedException
1624	remoteAddress	127.0.0.1
1625	message	Access is denied
1625	type	org.springframework.security.access.AccessDeniedException
1626	message	Access is denied
1626	type	org.springframework.security.access.AccessDeniedException
1627	remoteAddress	127.0.0.1
1628	message	Access is denied
1628	type	org.springframework.security.access.AccessDeniedException
1629	message	Access is denied
1629	type	org.springframework.security.access.AccessDeniedException
1630	remoteAddress	127.0.0.1
1631	message	Access is denied
1631	type	org.springframework.security.access.AccessDeniedException
1632	remoteAddress	127.0.0.1
1633	message	Access is denied
1633	type	org.springframework.security.access.AccessDeniedException
1634	message	Access is denied
1634	type	org.springframework.security.access.AccessDeniedException
1635	remoteAddress	127.0.0.1
1636	message	Access is denied
1636	type	org.springframework.security.access.AccessDeniedException
1637	message	Access is denied
1637	type	org.springframework.security.access.AccessDeniedException
1638	remoteAddress	127.0.0.1
1639	message	Access is denied
1639	type	org.springframework.security.access.AccessDeniedException
1640	message	Access is denied
1640	type	org.springframework.security.access.AccessDeniedException
1641	remoteAddress	127.0.0.1
1642	message	Access is denied
1642	type	org.springframework.security.access.AccessDeniedException
1643	message	Access is denied
1643	type	org.springframework.security.access.AccessDeniedException
1644	remoteAddress	127.0.0.1
1645	remoteAddress	127.0.0.1
1646	message	Access is denied
1646	type	org.springframework.security.access.AccessDeniedException
1647	message	Access is denied
1647	type	org.springframework.security.access.AccessDeniedException
1648	message	Access is denied
1648	type	org.springframework.security.access.AccessDeniedException
1649	sessionId	44DDA3B4158C50810745B19966B6F590
1649	remoteAddress	127.0.0.1
1650	message	Access is denied
1650	type	org.springframework.security.access.AccessDeniedException
1651	message	Access is denied
1651	type	org.springframework.security.access.AccessDeniedException
1652	remoteAddress	127.0.0.1
1653	message	Access is denied
1653	type	org.springframework.security.access.AccessDeniedException
1654	message	Access is denied
1654	type	org.springframework.security.access.AccessDeniedException
1655	remoteAddress	127.0.0.1
1656	message	Access is denied
1656	type	org.springframework.security.access.AccessDeniedException
1657	message	Access is denied
1657	type	org.springframework.security.access.AccessDeniedException
1658	remoteAddress	127.0.0.1
1659	message	Access is denied
1659	type	org.springframework.security.access.AccessDeniedException
1660	message	Access is denied
1660	type	org.springframework.security.access.AccessDeniedException
1661	remoteAddress	127.0.0.1
1662	application.applicationId	1383
1662	application.approvalState	CHANGE_REQUESTED
1662	application.name	able933@mailinator.com
1663	message	Access is denied
1664	message	Access is denied
1664	type	org.springframework.security.access.AccessDeniedException
1665	remoteAddress	127.0.0.1
1666	application.applicationId	1570
1666	application.approvalState	CHANGE_REQUESTED
1666	application.name	938 Org
1667	remoteAddress	127.0.0.1
1668	message	Access is denied
1668	type	org.springframework.security.access.AccessDeniedException
1669	message	Access is denied
1669	type	org.springframework.security.access.AccessDeniedException
1670	remoteAddress	127.0.0.1
1671	message	Access is denied
1671	type	org.springframework.security.access.AccessDeniedException
1672	message	Access is denied
1672	type	org.springframework.security.access.AccessDeniedException
1673	remoteAddress	127.0.0.1
1674	message	Access is denied
1674	type	org.springframework.security.access.AccessDeniedException
1675	message	Access is denied
1675	type	org.springframework.security.access.AccessDeniedException
1676	remoteAddress	127.0.0.1
1677	message	Access is denied
1677	type	org.springframework.security.access.AccessDeniedException
1678	message	Access is denied
1678	type	org.springframework.security.access.AccessDeniedException
1679	remoteAddress	127.0.0.1
1680	message	Access is denied
1680	type	org.springframework.security.access.AccessDeniedException
1681	message	Access is denied
1681	type	org.springframework.security.access.AccessDeniedException
1682	remoteAddress	127.0.0.1
1683	message	Access is denied
1683	type	org.springframework.security.access.AccessDeniedException
1684	message	Access is denied
1684	type	org.springframework.security.access.AccessDeniedException
1685	remoteAddress	127.0.0.1
1686	message	Access is denied
1686	type	org.springframework.security.access.AccessDeniedException
1687	message	Access is denied
1687	type	org.springframework.security.access.AccessDeniedException
1688	remoteAddress	127.0.0.1
1689	message	Access is denied
1689	type	org.springframework.security.access.AccessDeniedException
1690	message	Access is denied
1690	type	org.springframework.security.access.AccessDeniedException
1691	remoteAddress	127.0.0.1
1692	message	Access is denied
1692	type	org.springframework.security.access.AccessDeniedException
1693	message	Access is denied
1693	type	org.springframework.security.access.AccessDeniedException
1694	remoteAddress	127.0.0.1
1695	message	Access is denied
1695	type	org.springframework.security.access.AccessDeniedException
1696	message	Access is denied
1696	type	org.springframework.security.access.AccessDeniedException
1697	remoteAddress	127.0.0.1
1698	message	Access is denied
1698	type	org.springframework.security.access.AccessDeniedException
1699	message	Access is denied
1699	type	org.springframework.security.access.AccessDeniedException
1700	remoteAddress	127.0.0.1
1701	message	Access is denied
1701	type	org.springframework.security.access.AccessDeniedException
1702	message	Access is denied
1702	type	org.springframework.security.access.AccessDeniedException
1703	remoteAddress	127.0.0.1
1705	application.applicationId	1494
1705	application.approvalState	APPROVED
1705	application.name	936 org
1706	message	Access is denied
1706	type	org.springframework.security.access.AccessDeniedException
1707	message	Access is denied
1707	type	org.springframework.security.access.AccessDeniedException
1708	remoteAddress	127.0.0.1
1709	remoteAddress	127.0.0.1
1710	message	Access is denied
1710	type	org.springframework.security.access.AccessDeniedException
1711	remoteAddress	127.0.0.1
1712	message	An Authentication object was not found in the SecurityContext
1712	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
1713	message	Access is denied
1713	type	org.springframework.security.access.AccessDeniedException
1714	remoteAddress	127.0.0.1
1715	message	Access is denied
1715	type	org.springframework.security.access.AccessDeniedException
1716	message	Access is denied
1716	type	org.springframework.security.access.AccessDeniedException
1717	remoteAddress	127.0.0.1
1718	message	Access is denied
1718	type	org.springframework.security.access.AccessDeniedException
1719	message	Access is denied
1719	type	org.springframework.security.access.AccessDeniedException
1720	remoteAddress	127.0.0.1
1721	remoteAddress	127.0.0.1
1722	message	Access is denied
1722	type	org.springframework.security.access.AccessDeniedException
1723	remoteAddress	127.0.0.1
1724	message	Access is denied
1724	type	org.springframework.security.access.AccessDeniedException
1725	message	Access is denied
1725	type	org.springframework.security.access.AccessDeniedException
1726	remoteAddress	127.0.0.1
1727	message	Access is denied
1727	type	org.springframework.security.access.AccessDeniedException
1728	message	Access is denied
1728	type	org.springframework.security.access.AccessDeniedException
1729	remoteAddress	127.0.0.1
1730	message	Access is denied
1730	type	org.springframework.security.access.AccessDeniedException
1735	affiliate.creator	able939@mailinator.com
1736	message	Access is denied
1736	type	org.springframework.security.access.AccessDeniedException
1737	message	Access is denied
1737	type	org.springframework.security.access.AccessDeniedException
1738	remoteAddress	127.0.0.1
1739	message	Access is denied
1739	type	org.springframework.security.access.AccessDeniedException
1740	message	Access is denied
1740	type	org.springframework.security.access.AccessDeniedException
1741	message	Access is denied
1741	type	org.springframework.security.access.AccessDeniedException
1742	message	Access is denied
1742	type	org.springframework.security.access.AccessDeniedException
1743	remoteAddress	127.0.0.1
1744	remoteAddress	127.0.0.1
1745	remoteAddress	127.0.0.1
1746	message	Access is denied
1746	type	org.springframework.security.access.AccessDeniedException
1747	message	Access is denied
1747	type	org.springframework.security.access.AccessDeniedException
1748	remoteAddress	127.0.0.1
1751	remoteAddress	127.0.0.1
1752	application.applicationId	1732
1752	application.approvalState	SUBMITTED
1752	application.name	939 Org
1753	message	Access is denied
1753	type	org.springframework.security.access.AccessDeniedException
1754	message	Access is denied
1754	type	org.springframework.security.access.AccessDeniedException
1755	remoteAddress	127.0.0.1
1756	application.applicationId	1732
1756	application.approvalState	CHANGE_REQUESTED
1756	application.name	939 Org
1757	message	Access is denied
1757	type	org.springframework.security.access.AccessDeniedException
1758	message	Access is denied
1758	type	org.springframework.security.access.AccessDeniedException
1759	remoteAddress	127.0.0.1
1760	application.applicationId	1732
1760	application.approvalState	RESUBMITTED
1760	application.name	939 Org
1761	message	Access is denied
1761	type	org.springframework.security.access.AccessDeniedException
1762	message	Access is denied
1762	type	org.springframework.security.access.AccessDeniedException
1763	remoteAddress	127.0.0.1
1764	message	Access is denied
1764	type	org.springframework.security.access.AccessDeniedException
1765	message	Access is denied
1765	type	org.springframework.security.access.AccessDeniedException
1766	remoteAddress	127.0.0.1
1767	message	Access is denied
1767	type	org.springframework.security.access.AccessDeniedException
1768	message	Access is denied
1768	type	org.springframework.security.access.AccessDeniedException
1769	remoteAddress	127.0.0.1
1770	application.applicationId	1732
1770	application.approvalState	CHANGE_REQUESTED
1770	application.name	939 Org
1771	message	Access is denied
1771	type	org.springframework.security.access.AccessDeniedException
1772	message	Access is denied
1772	type	org.springframework.security.access.AccessDeniedException
1773	remoteAddress	127.0.0.1
1774	application.applicationId	1732
1774	application.approvalState	RESUBMITTED
1774	application.name	939 Org
1775	message	Access is denied
1775	type	org.springframework.security.access.AccessDeniedException
1776	message	Access is denied
1776	type	org.springframework.security.access.AccessDeniedException
1777	remoteAddress	127.0.0.1
1778	message	Access is denied
1778	type	org.springframework.security.access.AccessDeniedException
1779	message	Access is denied
1779	type	org.springframework.security.access.AccessDeniedException
1780	remoteAddress	127.0.0.1
1781	message	Access is denied
1781	type	org.springframework.security.access.AccessDeniedException
1782	message	Access is denied
1782	type	org.springframework.security.access.AccessDeniedException
1783	remoteAddress	127.0.0.1
1784	application.applicationId	1732
1784	application.approvalState	CHANGE_REQUESTED
1784	application.name	939 Org
1785	message	Access is denied
1785	type	org.springframework.security.access.AccessDeniedException
1786	message	Access is denied
1786	type	org.springframework.security.access.AccessDeniedException
1787	remoteAddress	127.0.0.1
1788	application.applicationId	1732
1788	application.approvalState	RESUBMITTED
1788	application.name	939 Org
1789	message	Access is denied
1789	type	org.springframework.security.access.AccessDeniedException
1790	message	Access is denied
1790	type	org.springframework.security.access.AccessDeniedException
1791	remoteAddress	127.0.0.1
1792	application.applicationId	1732
1792	application.approvalState	CHANGE_REQUESTED
1792	application.name	939 Org
1794	message	Access is denied
1794	type	org.springframework.security.access.AccessDeniedException
1793	message	Access is denied
1793	type	org.springframework.security.access.AccessDeniedException
1795	remoteAddress	127.0.0.1
1798	application.applicationId	1732
1798	application.approvalState	RESUBMITTED
1798	application.name	939 Org
1799	message	Access is denied
1799	type	org.springframework.security.access.AccessDeniedException
1800	message	Access is denied
1800	type	org.springframework.security.access.AccessDeniedException
1801	remoteAddress	127.0.0.1
1802	application.applicationId	1732
1802	application.approvalState	CHANGE_REQUESTED
1802	application.name	939 Org
1803	message	Access is denied
1803	type	org.springframework.security.access.AccessDeniedException
1804	message	Access is denied
1804	type	org.springframework.security.access.AccessDeniedException
1805	remoteAddress	127.0.0.1
1806	application.applicationId	1732
1806	application.approvalState	RESUBMITTED
1806	application.name	939 Org
1807	message	Access is denied
1807	type	org.springframework.security.access.AccessDeniedException
1808	message	Access is denied
1808	type	org.springframework.security.access.AccessDeniedException
1809	remoteAddress	127.0.0.1
1811	application.applicationId	1732
1811	application.approvalState	APPROVED
1811	application.name	939 Org
1812	message	Access is denied
1812	type	org.springframework.security.access.AccessDeniedException
1813	message	Access is denied
1813	type	org.springframework.security.access.AccessDeniedException
1814	remoteAddress	127.0.0.1
1815	message	Access is denied
1815	type	org.springframework.security.access.AccessDeniedException
1816	message	Access is denied
1816	type	org.springframework.security.access.AccessDeniedException
1817	message	Access is denied
1817	type	org.springframework.security.access.AccessDeniedException
1822	affiliate.creator	able940@mailinator.com
1823	message	Access is denied
1823	type	org.springframework.security.access.AccessDeniedException
1824	message	Access is denied
1824	type	org.springframework.security.access.AccessDeniedException
1825	application.applicationId	1819
1825	application.approvalState	SUBMITTED
1825	application.name	
1826	message	Access is denied
1826	type	org.springframework.security.access.AccessDeniedException
1827	message	Access is denied
1827	type	org.springframework.security.access.AccessDeniedException
1828	remoteAddress	127.0.0.1
1830	application.applicationId	1819
1830	application.approvalState	APPROVED
1830	application.name	
1831	message	Access is denied
1831	type	org.springframework.security.access.AccessDeniedException
1832	message	Access is denied
1832	type	org.springframework.security.access.AccessDeniedException
1833	remoteAddress	127.0.0.1
1834	message	Access is denied
1834	type	org.springframework.security.access.AccessDeniedException
1835	message	Access is denied
1835	type	org.springframework.security.access.AccessDeniedException
1836	message	Access is denied
1836	type	org.springframework.security.access.AccessDeniedException
1837	message	Access is denied
1837	type	org.springframework.security.access.AccessDeniedException
1842	affiliate.creator	able941@mailinator.com
1843	message	Access is denied
1843	type	org.springframework.security.access.AccessDeniedException
1844	message	Access is denied
1844	type	org.springframework.security.access.AccessDeniedException
1845	application.applicationId	1839
1845	application.approvalState	SUBMITTED
1845	application.name	941 org acad
1846	message	Access is denied
1846	type	org.springframework.security.access.AccessDeniedException
1847	message	Access is denied
1847	type	org.springframework.security.access.AccessDeniedException
1848	remoteAddress	127.0.0.1
1849	message	Access is denied
1849	type	org.springframework.security.access.AccessDeniedException
1850	message	Access is denied
1850	type	org.springframework.security.access.AccessDeniedException
1851	remoteAddress	127.0.0.1
1853	application.applicationId	1839
1853	application.approvalState	APPROVED
1853	application.name	941 org acad
1854	message	Access is denied
1854	type	org.springframework.security.access.AccessDeniedException
1855	message	Access is denied
1855	type	org.springframework.security.access.AccessDeniedException
1856	remoteAddress	127.0.0.1
1857	message	Access is denied
1857	type	org.springframework.security.access.AccessDeniedException
1862	affiliate.creator	able942@mailinator.com
1863	message	Access is denied
1863	type	org.springframework.security.access.AccessDeniedException
1864	message	Access is denied
1864	type	org.springframework.security.access.AccessDeniedException
1865	application.applicationId	1859
1865	application.approvalState	SUBMITTED
1865	application.name	942 org commercial
1866	message	Access is denied
1866	type	org.springframework.security.access.AccessDeniedException
1868	remoteAddress	127.0.0.1
1871	message	Access is denied
1871	type	org.springframework.security.access.AccessDeniedException
1873	remoteAddress	127.0.0.1
1867	message	Access is denied
1867	type	org.springframework.security.access.AccessDeniedException
1870	application.applicationId	1859
1870	application.approvalState	APPROVED
1870	application.name	942 org commercial
1872	message	Access is denied
1872	type	org.springframework.security.access.AccessDeniedException
1874	message	Access is denied
1874	type	org.springframework.security.access.AccessDeniedException
1875	message	Access is denied
1875	type	org.springframework.security.access.AccessDeniedException
1876	remoteAddress	127.0.0.1
1877	message	Access is denied
1877	type	org.springframework.security.access.AccessDeniedException
1878	message	Access is denied
1878	type	org.springframework.security.access.AccessDeniedException
1879	remoteAddress	127.0.0.1
1880	application.applicationId	1570
1880	application.approvalState	CHANGE_REQUESTED
1880	application.name	938 Org
1881	message	Access is denied
1881	type	org.springframework.security.access.AccessDeniedException
1882	message	Access is denied
1882	type	org.springframework.security.access.AccessDeniedException
1883	remoteAddress	127.0.0.1
1884	message	Access is denied
1884	type	org.springframework.security.access.AccessDeniedException
1885	remoteAddress	127.0.0.1
1886	remoteAddress	127.0.0.1
1887	message	Access is denied
1887	type	org.springframework.security.access.AccessDeniedException
1888	message	Access is denied
1888	type	org.springframework.security.access.AccessDeniedException
1889	remoteAddress	127.0.0.1
1890	remoteAddress	127.0.0.1
1892	releasePackage.name	Sweden Z
1894	releaseVersion.name	Vers 1
1896	releaseFile.label	a
1898	releaseFile.label	b
1900	releaseVersion.name	Ver 2
1902	releaseFile.label	c
1904	releaseFile.label	d
1905	releaseVersion.name	Ver 2
1906	releaseVersion.name	Vers 1
1907	releaseVersion.name	Ver 2
1908	releaseVersion.name	Ver 2
1910	releasePackage.name	Sweden A
1912	releaseVersion.name	VerA 1
1914	releaseFile.label	a
1915	releaseVersion.name	VerA 1
1917	releasePackage.name	Sweden Offline
1919	releaseVersion.name	Ver 1 OFF
1921	releaseFile.label	a
1922	message	Access is denied
1922	type	org.springframework.security.access.AccessDeniedException
1923	message	Access is denied
1923	type	org.springframework.security.access.AccessDeniedException
1924	message	Access is denied
1924	type	org.springframework.security.access.AccessDeniedException
1925	message	Access is denied
1925	type	org.springframework.security.access.AccessDeniedException
1926	remoteAddress	127.0.0.1
1927	remoteAddress	127.0.0.1
1928	message	Access is denied
1928	type	org.springframework.security.access.AccessDeniedException
1929	message	Access is denied
1929	type	org.springframework.security.access.AccessDeniedException
1930	sessionId	B8937765BE0779ADE65887054D6FDAFC
1930	remoteAddress	127.0.0.1
1931	message	Access is denied
1931	type	org.springframework.security.access.AccessDeniedException
1932	message	Access is denied
1932	type	org.springframework.security.access.AccessDeniedException
1933	sessionId	03EB422AD087B332911B6EDC6346C6F8
1933	remoteAddress	127.0.0.1
1934	remoteAddress	127.0.0.1
1935	remoteAddress	127.0.0.1
1936	message	Access is denied
1936	type	org.springframework.security.access.AccessDeniedException
1937	message	Access is denied
1937	type	org.springframework.security.access.AccessDeniedException
1938	message	Access is denied
1938	type	org.springframework.security.access.AccessDeniedException
1939	message	Access is denied
1939	type	org.springframework.security.access.AccessDeniedException
1940	remoteAddress	127.0.0.1
1941	message	Access is denied
1941	type	org.springframework.security.access.AccessDeniedException
1942	application.applicationId	1513
1942	application.approvalState	CHANGE_REQUESTED
1942	application.name	937 Org
1943	message	Access is denied
1943	type	org.springframework.security.access.AccessDeniedException
1944	message	Access is denied
1944	type	org.springframework.security.access.AccessDeniedException
1945	message	Access is denied
1945	type	org.springframework.security.access.AccessDeniedException
1946	remoteAddress	127.0.0.1
1947	message	Access is denied
1947	type	org.springframework.security.access.AccessDeniedException
1948	message	Access is denied
1948	type	org.springframework.security.access.AccessDeniedException
1949	remoteAddress	127.0.0.1
1950	message	Access is denied
1950	type	org.springframework.security.access.AccessDeniedException
1951	message	Access is denied
1952	message	Access is denied
1951	type	org.springframework.security.access.AccessDeniedException
1952	type	org.springframework.security.access.AccessDeniedException
1953	message	Access is denied
1953	type	org.springframework.security.access.AccessDeniedException
1954	message	Access is denied
1954	type	org.springframework.security.access.AccessDeniedException
1956	message	Access is denied
1956	type	org.springframework.security.access.AccessDeniedException
1957	message	Access is denied
1957	type	org.springframework.security.access.AccessDeniedException
1958	remoteAddress	127.0.0.1
1959	message	Access is denied
1959	type	org.springframework.security.access.AccessDeniedException
1960	message	Access is denied
1960	type	org.springframework.security.access.AccessDeniedException
1961	message	Access is denied
1961	type	org.springframework.security.access.AccessDeniedException
1962	message	Access is denied
1962	type	org.springframework.security.access.AccessDeniedException
1963	remoteAddress	127.0.0.1
1955	remoteAddress	127.0.0.1
1964	message	Access is denied
1964	type	org.springframework.security.access.AccessDeniedException
1965	remoteAddress	127.0.0.1
1966	message	Access is denied
1966	type	org.springframework.security.access.AccessDeniedException
1967	message	Access is denied
1967	type	org.springframework.security.access.AccessDeniedException
1968	message	Access is denied
1968	type	org.springframework.security.access.AccessDeniedException
1969	message	Access is denied
1969	type	org.springframework.security.access.AccessDeniedException
1970	message	Access is denied
1970	type	org.springframework.security.access.AccessDeniedException
1971	message	Access is denied
1972	message	Access is denied
1972	type	org.springframework.security.access.AccessDeniedException
1971	type	org.springframework.security.access.AccessDeniedException
1973	message	Access is denied
1973	type	org.springframework.security.access.AccessDeniedException
1974	message	Access is denied
1975	message	Access is denied
1974	type	org.springframework.security.access.AccessDeniedException
1975	type	org.springframework.security.access.AccessDeniedException
1976	message	Access is denied
1976	type	org.springframework.security.access.AccessDeniedException
1977	message	Access is denied
1977	type	org.springframework.security.access.AccessDeniedException
1978	message	Access is denied
1978	type	org.springframework.security.access.AccessDeniedException
1979	message	Access is denied
1979	type	org.springframework.security.access.AccessDeniedException
1980	sessionId	3716227645462096EFFAB70E115B0C1C
1980	remoteAddress	127.0.0.1
1981	message	Access is denied
1981	type	org.springframework.security.access.AccessDeniedException
1982	message	Access is denied
1982	type	org.springframework.security.access.AccessDeniedException
1984	message	Access is denied
1983	message	Access is denied
1984	type	org.springframework.security.access.AccessDeniedException
1983	type	org.springframework.security.access.AccessDeniedException
1985	message	Access is denied
1985	type	org.springframework.security.access.AccessDeniedException
1986	message	Access is denied
1987	message	Access is denied
1986	type	org.springframework.security.access.AccessDeniedException
1987	type	org.springframework.security.access.AccessDeniedException
1988	message	Access is denied
1988	type	org.springframework.security.access.AccessDeniedException
1989	message	Access is denied
1989	type	org.springframework.security.access.AccessDeniedException
1990	message	Access is denied
1990	type	org.springframework.security.access.AccessDeniedException
1991	message	Access is denied
1991	type	org.springframework.security.access.AccessDeniedException
1993	message	Access is denied
1992	message	Access is denied
1993	type	org.springframework.security.access.AccessDeniedException
1992	type	org.springframework.security.access.AccessDeniedException
1994	message	Access is denied
1994	type	org.springframework.security.access.AccessDeniedException
1996	message	Access is denied
1996	type	org.springframework.security.access.AccessDeniedException
1995	message	Access is denied
1995	type	org.springframework.security.access.AccessDeniedException
1997	message	Access is denied
1997	type	org.springframework.security.access.AccessDeniedException
1998	message	Access is denied
1998	type	org.springframework.security.access.AccessDeniedException
1999	message	Access is denied
1999	type	org.springframework.security.access.AccessDeniedException
2000	message	Access is denied
2000	type	org.springframework.security.access.AccessDeniedException
2001	message	Access is denied
2001	type	org.springframework.security.access.AccessDeniedException
2002	message	Access is denied
2002	type	org.springframework.security.access.AccessDeniedException
2003	message	Access is denied
2003	type	org.springframework.security.access.AccessDeniedException
2004	message	Access is denied
2004	type	org.springframework.security.access.AccessDeniedException
2005	message	Access is denied
2005	type	org.springframework.security.access.AccessDeniedException
2006	message	Access is denied
2006	type	org.springframework.security.access.AccessDeniedException
2007	message	Access is denied
2007	type	org.springframework.security.access.AccessDeniedException
2012	affiliate.creator	able943@mailinator.com
2013	message	Access is denied
2013	type	org.springframework.security.access.AccessDeniedException
2014	message	Access is denied
2014	type	org.springframework.security.access.AccessDeniedException
2015	message	Access is denied
2015	type	org.springframework.security.access.AccessDeniedException
2016	message	Access is denied
2016	type	org.springframework.security.access.AccessDeniedException
2017	message	Access is denied
2017	type	org.springframework.security.access.AccessDeniedException
2018	message	Access is denied
2018	type	org.springframework.security.access.AccessDeniedException
2019	message	Access is denied
2019	type	org.springframework.security.access.AccessDeniedException
2021	message	Access is denied
2021	type	org.springframework.security.access.AccessDeniedException
2020	message	Access is denied
2020	type	org.springframework.security.access.AccessDeniedException
2022	message	Access is denied
2022	type	org.springframework.security.access.AccessDeniedException
2023	message	Access is denied
2023	type	org.springframework.security.access.AccessDeniedException
2024	message	Access is denied
2024	type	org.springframework.security.access.AccessDeniedException
2025	message	Access is denied
2025	type	org.springframework.security.access.AccessDeniedException
2026	message	Access is denied
2026	type	org.springframework.security.access.AccessDeniedException
2027	message	Access is denied
2027	type	org.springframework.security.access.AccessDeniedException
2028	message	Access is denied
2028	type	org.springframework.security.access.AccessDeniedException
2029	message	Access is denied
2029	type	org.springframework.security.access.AccessDeniedException
2030	message	Access is denied
2030	type	org.springframework.security.access.AccessDeniedException
2031	message	Access is denied
2031	type	org.springframework.security.access.AccessDeniedException
2032	message	Access is denied
2032	type	org.springframework.security.access.AccessDeniedException
2037	affiliate.creator	able944@mailinator.com
2038	message	Access is denied
2038	type	org.springframework.security.access.AccessDeniedException
2039	message	Access is denied
2039	type	org.springframework.security.access.AccessDeniedException
2040	message	Access is denied
2040	type	org.springframework.security.access.AccessDeniedException
2041	message	Access is denied
2041	type	org.springframework.security.access.AccessDeniedException
2042	message	Access is denied
2042	type	org.springframework.security.access.AccessDeniedException
2043	message	Access is denied
2043	type	org.springframework.security.access.AccessDeniedException
2044	remoteAddress	127.0.0.1
2050	message	Access is denied
2050	type	org.springframework.security.access.AccessDeniedException
2051	message	Access is denied
2051	type	org.springframework.security.access.AccessDeniedException
2052	message	Access is denied
2052	type	org.springframework.security.access.AccessDeniedException
2057	affiliate.creator	me@bad1.com
2058	message	Access is denied
2058	type	org.springframework.security.access.AccessDeniedException
2059	message	Access is denied
2059	type	org.springframework.security.access.AccessDeniedException
2060	message	Access is denied
2060	type	org.springframework.security.access.AccessDeniedException
2061	message	Access is denied
2061	type	org.springframework.security.access.AccessDeniedException
2062	message	Access is denied
2062	type	org.springframework.security.access.AccessDeniedException
2063	message	Access is denied
2063	type	org.springframework.security.access.AccessDeniedException
2064	sessionId	30FE828EEB408104EF43B71AC195A405
2064	remoteAddress	127.0.0.1
2065	message	Access is denied
2065	type	org.springframework.security.access.AccessDeniedException
2066	message	Access is denied
2066	type	org.springframework.security.access.AccessDeniedException
2067	message	Access is denied
2067	type	org.springframework.security.access.AccessDeniedException
2068	remoteAddress	127.0.0.1
2069	message	Access is denied
2069	type	org.springframework.security.access.AccessDeniedException
2070	message	Access is denied
2070	type	org.springframework.security.access.AccessDeniedException
2071	message	Access is denied
2071	type	org.springframework.security.access.AccessDeniedException
2072	remoteAddress	127.0.0.1
2073	message	Access is denied
2073	type	org.springframework.security.access.AccessDeniedException
2074	message	Access is denied
2074	type	org.springframework.security.access.AccessDeniedException
2075	remoteAddress	127.0.0.1
2076	message	Access is denied
2076	type	org.springframework.security.access.AccessDeniedException
2077	remoteAddress	127.0.0.1
2078	message	Access is denied
2078	type	org.springframework.security.access.AccessDeniedException
2079	message	Access is denied
2079	type	org.springframework.security.access.AccessDeniedException
2080	remoteAddress	127.0.0.1
2081	message	Access is denied
2081	type	org.springframework.security.access.AccessDeniedException
2082	message	Access is denied
2082	type	org.springframework.security.access.AccessDeniedException
2083	remoteAddress	127.0.0.1
2084	message	Access is denied
2084	type	org.springframework.security.access.AccessDeniedException
2085	message	Access is denied
2085	type	org.springframework.security.access.AccessDeniedException
2086	remoteAddress	127.0.0.1
2087	message	Access is denied
2087	type	org.springframework.security.access.AccessDeniedException
2088	message	Access is denied
2088	type	org.springframework.security.access.AccessDeniedException
2089	message	Access is denied
2089	type	org.springframework.security.access.AccessDeniedException
2090	message	Access is denied
2090	type	org.springframework.security.access.AccessDeniedException
2091	remoteAddress	127.0.0.1
2097	message	Access is denied
2097	type	org.springframework.security.access.AccessDeniedException
2092	message	Access is denied
2092	type	org.springframework.security.access.AccessDeniedException
2093	message	Access is denied
2093	type	org.springframework.security.access.AccessDeniedException
2095	message	Access is denied
2095	type	org.springframework.security.access.AccessDeniedException
2096	message	Access is denied
2096	type	org.springframework.security.access.AccessDeniedException
2099	message	Access is denied
2099	type	org.springframework.security.access.AccessDeniedException
2100	message	Access is denied
2100	type	org.springframework.security.access.AccessDeniedException
2094	remoteAddress	127.0.0.1
2098	remoteAddress	127.0.0.1
2101	remoteAddress	127.0.0.1
2102	message	Access is denied
2102	type	org.springframework.security.access.AccessDeniedException
2103	remoteAddress	127.0.0.1
2104	message	Access is denied
2104	type	org.springframework.security.access.AccessDeniedException
2105	message	Access is denied
2105	type	org.springframework.security.access.AccessDeniedException
2106	message	Access is denied
2106	type	org.springframework.security.access.AccessDeniedException
2107	remoteAddress	127.0.0.1
2108	remoteAddress	127.0.0.1
2109	remoteAddress	127.0.0.1
2110	remoteAddress	127.0.0.1
2111	remoteAddress	127.0.0.1
2112	message	Access is denied
2112	type	org.springframework.security.access.AccessDeniedException
2113	remoteAddress	127.0.0.1
2114	message	Access is denied
2114	type	org.springframework.security.access.AccessDeniedException
2115	message	Access is denied
2115	type	org.springframework.security.access.AccessDeniedException
2116	message	Access is denied
2116	type	org.springframework.security.access.AccessDeniedException
2117	sessionId	D6FBC83A5C26B722D31731CEBA8AD50B
2117	remoteAddress	127.0.0.1
2118	message	Access is denied
2118	type	org.springframework.security.access.AccessDeniedException
2119	message	Access is denied
2119	type	org.springframework.security.access.AccessDeniedException
2124	affiliate.creator	able945@mailinator.com
2125	message	Access is denied
2125	type	org.springframework.security.access.AccessDeniedException
2126	message	Access is denied
2126	type	org.springframework.security.access.AccessDeniedException
2127	message	Access is denied
2127	type	org.springframework.security.access.AccessDeniedException
2128	message	Access is denied
2128	type	org.springframework.security.access.AccessDeniedException
2129	message	Access is denied
2129	type	org.springframework.security.access.AccessDeniedException
2134	affiliate.creator	able946@mailinator.com
2135	message	Access is denied
2135	type	org.springframework.security.access.AccessDeniedException
2136	message	Access is denied
2136	type	org.springframework.security.access.AccessDeniedException
2140	application.applicationId	2132
2140	application.approvalState	SUBMITTED
2140	application.name	946 Org
2141	message	An Authentication object was not found in the SecurityContext
2141	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2142	message	Access is denied
2142	type	org.springframework.security.access.AccessDeniedException
2143	message	Access is denied
2143	type	org.springframework.security.access.AccessDeniedException
2144	remoteAddress	127.0.0.1
2145	message	Access is denied
2145	type	org.springframework.security.access.AccessDeniedException
2147	application.applicationId	2132
2147	application.approvalState	APPROVED
2147	application.name	946 Org
2148	message	Access is denied
2148	type	org.springframework.security.access.AccessDeniedException
2149	message	Access is denied
2149	type	org.springframework.security.access.AccessDeniedException
2150	message	Access is denied
2150	type	org.springframework.security.access.AccessDeniedException
2151	remoteAddress	127.0.0.1
2152	remoteAddress	127.0.0.1
2162	message	Access is denied
2162	type	org.springframework.security.access.AccessDeniedException
2163	remoteAddress	127.0.0.1
2164	message	Access is denied
2164	type	org.springframework.security.access.AccessDeniedException
2165	message	Access is denied
2165	type	org.springframework.security.access.AccessDeniedException
2166	message	Access is denied
2166	type	org.springframework.security.access.AccessDeniedException
2167	message	Access is denied
2167	type	org.springframework.security.access.AccessDeniedException
2168	message	Access is denied
2168	type	org.springframework.security.access.AccessDeniedException
2173	affiliate.creator	able947@mailinator.com
2174	message	Access is denied
2174	type	org.springframework.security.access.AccessDeniedException
2175	message	Access is denied
2175	type	org.springframework.security.access.AccessDeniedException
2191	application.applicationId	2171
2191	application.approvalState	SUBMITTED
2191	application.name	947 Org
2192	message	An Authentication object was not found in the SecurityContext
2192	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2193	message	Access is denied
2193	type	org.springframework.security.access.AccessDeniedException
2194	message	Access is denied
2194	type	org.springframework.security.access.AccessDeniedException
2195	remoteAddress	127.0.0.1
2196	message	Access is denied
2196	type	org.springframework.security.access.AccessDeniedException
2197	application.applicationId	2171
2197	application.approvalState	CHANGE_REQUESTED
2197	application.name	947 Org
2199	message	Access is denied
2199	type	org.springframework.security.access.AccessDeniedException
2206	message	Access is denied
2206	type	org.springframework.security.access.AccessDeniedException
2210	message	Access is denied
2210	type	org.springframework.security.access.AccessDeniedException
2198	message	Access is denied
2198	type	org.springframework.security.access.AccessDeniedException
2200	message	Access is denied
2200	type	org.springframework.security.access.AccessDeniedException
2204	message	Access is denied
2204	type	org.springframework.security.access.AccessDeniedException
2208	application.applicationId	2171
2208	application.approvalState	APPROVED
2208	application.name	947 Org
2209	message	Access is denied
2209	type	org.springframework.security.access.AccessDeniedException
2212	remoteAddress	127.0.0.1
2201	remoteAddress	127.0.0.1
2203	message	Access is denied
2203	type	org.springframework.security.access.AccessDeniedException
2202	application.applicationId	2171
2202	application.approvalState	RESUBMITTED
2202	application.name	947 Org
2211	message	Access is denied
2211	type	org.springframework.security.access.AccessDeniedException
2205	remoteAddress	127.0.0.1
2213	remoteAddress	127.0.0.1
2215	message	Access is denied
2215	type	org.springframework.security.access.AccessDeniedException
2216	remoteAddress	127.0.0.1
2217	message	Access is denied
2217	type	org.springframework.security.access.AccessDeniedException
2222	affiliate.creator	able948@mailinator.com
2223	message	Access is denied
2223	type	org.springframework.security.access.AccessDeniedException
2224	message	Access is denied
2224	type	org.springframework.security.access.AccessDeniedException
2226	application.applicationId	2220
2226	application.approvalState	SUBMITTED
2226	application.name	
2227	message	An Authentication object was not found in the SecurityContext
2227	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2228	message	Access is denied
2228	type	org.springframework.security.access.AccessDeniedException
2229	message	Access is denied
2229	type	org.springframework.security.access.AccessDeniedException
2230	remoteAddress	127.0.0.1
2231	message	Access is denied
2231	type	org.springframework.security.access.AccessDeniedException
2232	application.applicationId	2220
2232	application.approvalState	CHANGE_REQUESTED
2232	application.name	
2233	message	Access is denied
2233	type	org.springframework.security.access.AccessDeniedException
2234	message	Access is denied
2234	type	org.springframework.security.access.AccessDeniedException
2235	message	Access is denied
2235	type	org.springframework.security.access.AccessDeniedException
2236	message	Access is denied
2236	type	org.springframework.security.access.AccessDeniedException
2237	remoteAddress	127.0.0.1
2238	message	Access is denied
2238	type	org.springframework.security.access.AccessDeniedException
2239	message	Access is denied
2239	type	org.springframework.security.access.AccessDeniedException
2240	remoteAddress	127.0.0.1
2242	application.applicationId	2220
2242	application.approvalState	RESUBMITTED
2242	application.name	
2243	remoteAddress	127.0.0.1
2244	message	Access is denied
2244	type	org.springframework.security.access.AccessDeniedException
2245	message	Access is denied
2245	type	org.springframework.security.access.AccessDeniedException
2246	message	Access is denied
2246	type	org.springframework.security.access.AccessDeniedException
2247	message	Access is denied
2247	type	org.springframework.security.access.AccessDeniedException
2248	remoteAddress	127.0.0.1
2250	application.applicationId	2220
2250	application.approvalState	APPROVED
2250	application.name	
2251	message	Access is denied
2251	type	org.springframework.security.access.AccessDeniedException
2252	message	Access is denied
2252	type	org.springframework.security.access.AccessDeniedException
2253	message	Access is denied
2253	type	org.springframework.security.access.AccessDeniedException
2254	remoteAddress	127.0.0.1
2255	message	An Authentication object was not found in the SecurityContext
2255	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2256	message	Access is denied
2256	type	org.springframework.security.access.AccessDeniedException
2257	message	Access is denied
2257	type	org.springframework.security.access.AccessDeniedException
2258	message	Access is denied
2258	type	org.springframework.security.access.AccessDeniedException
2259	message	Access is denied
2259	type	org.springframework.security.access.AccessDeniedException
2260	message	Access is denied
2260	type	org.springframework.security.access.AccessDeniedException
2261	remoteAddress	127.0.0.1
2262	remoteAddress	127.0.0.1
2263	remoteAddress	127.0.0.1
2264	message	Access is denied
2264	type	org.springframework.security.access.AccessDeniedException
2265	message	Access is denied
2266	message	Access is denied
2265	type	org.springframework.security.access.AccessDeniedException
2266	type	org.springframework.security.access.AccessDeniedException
2267	message	Access is denied
2267	type	org.springframework.security.access.AccessDeniedException
2268	message	Access is denied
2268	type	org.springframework.security.access.AccessDeniedException
2269	message	Access is denied
2269	type	org.springframework.security.access.AccessDeniedException
2270	message	Access is denied
2270	type	org.springframework.security.access.AccessDeniedException
2271	message	Access is denied
2271	type	org.springframework.security.access.AccessDeniedException
2272	message	Access is denied
2272	type	org.springframework.security.access.AccessDeniedException
2273	message	Access is denied
2273	type	org.springframework.security.access.AccessDeniedException
2274	message	Access is denied
2274	type	org.springframework.security.access.AccessDeniedException
2275	message	Access is denied
2275	type	org.springframework.security.access.AccessDeniedException
2276	message	Access is denied
2276	type	org.springframework.security.access.AccessDeniedException
2277	message	Access is denied
2277	type	org.springframework.security.access.AccessDeniedException
2278	message	Access is denied
2278	type	org.springframework.security.access.AccessDeniedException
2279	message	Access is denied
2279	type	org.springframework.security.access.AccessDeniedException
2280	message	Access is denied
2280	type	org.springframework.security.access.AccessDeniedException
2281	message	Access is denied
2281	type	org.springframework.security.access.AccessDeniedException
2282	message	Access is denied
2282	type	org.springframework.security.access.AccessDeniedException
2283	message	Access is denied
2283	type	org.springframework.security.access.AccessDeniedException
2284	message	Access is denied
2284	type	org.springframework.security.access.AccessDeniedException
2285	message	Access is denied
2285	type	org.springframework.security.access.AccessDeniedException
2286	message	Access is denied
2286	type	org.springframework.security.access.AccessDeniedException
2287	message	Access is denied
2287	type	org.springframework.security.access.AccessDeniedException
2288	message	Access is denied
2288	type	org.springframework.security.access.AccessDeniedException
2289	message	Access is denied
2289	type	org.springframework.security.access.AccessDeniedException
2290	message	Access is denied
2290	type	org.springframework.security.access.AccessDeniedException
2291	message	Access is denied
2291	type	org.springframework.security.access.AccessDeniedException
2292	sessionId	500B02AFE6E6F4402EE4B3A4066764B1
2292	remoteAddress	127.0.0.1
2293	message	An Authentication object was not found in the SecurityContext
2293	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2294	message	Access is denied
2294	type	org.springframework.security.access.AccessDeniedException
2295	remoteAddress	127.0.0.1
2300	affiliate.creator	able949@mailinator.com
2301	message	Access is denied
2301	type	org.springframework.security.access.AccessDeniedException
2302	message	Access is denied
2302	type	org.springframework.security.access.AccessDeniedException
2303	remoteAddress	127.0.0.1
2304	remoteAddress	127.0.0.1
2305	application.applicationId	2298
2305	application.approvalState	SUBMITTED
2305	application.name	949 Org
2306	message	Access is denied
2306	type	org.springframework.security.access.AccessDeniedException
2307	remoteAddress	127.0.0.1
2311	remoteAddress	127.0.0.1
2312	remoteAddress	127.0.0.1
2313	sessionId	E3742316C6EE6A2D8C88F9AF610888CA
2313	remoteAddress	127.0.0.1
2314	sessionId	7955B785F6136870ED8FB71D0F06D67E
2314	remoteAddress	127.0.0.1
2315	remoteAddress	127.0.0.1
2316	message	Access is denied
2316	type	org.springframework.security.access.AccessDeniedException
2317	message	Access is denied
2317	type	org.springframework.security.access.AccessDeniedException
2318	remoteAddress	127.0.0.1
2319	message	Access is denied
2319	type	org.springframework.security.access.AccessDeniedException
2321	releasePackage.name	Afgain A
2323	releaseVersion.name	AF Version 1
2325	releaseFile.label	1
2327	releaseFile.label	2
2328	releaseVersion.name	AF Version 1
2330	releaseVersion.name	AF Version 2
2332	releaseFile.label	3
2333	message	Access is denied
2333	type	org.springframework.security.access.AccessDeniedException
2334	message	Access is denied
2334	type	org.springframework.security.access.AccessDeniedException
2335	message	Access is denied
2335	type	org.springframework.security.access.AccessDeniedException
2336	remoteAddress	127.0.0.1
2337	message	Access is denied
2337	type	org.springframework.security.access.AccessDeniedException
2339	releasePackage.name	France A
2341	releaseVersion.name	FR v1
2343	releaseFile.label	1
2345	releaseFile.label	2
2346	releaseVersion.name	FR v1
2347	message	Access is denied
2347	type	org.springframework.security.access.AccessDeniedException
2348	message	Access is denied
2348	type	org.springframework.security.access.AccessDeniedException
2349	message	Access is denied
2349	type	org.springframework.security.access.AccessDeniedException
2350	remoteAddress	127.0.0.1
2351	message	Access is denied
2351	type	org.springframework.security.access.AccessDeniedException
2352	remoteAddress	127.0.0.1
2353	message	Access is denied
2353	type	org.springframework.security.access.AccessDeniedException
2354	message	Access is denied
2354	type	org.springframework.security.access.AccessDeniedException
2355	remoteAddress	127.0.0.1
2356	message	Access is denied
2672	message	Access is denied
2356	type	org.springframework.security.access.AccessDeniedException
2357	remoteAddress	127.0.0.1
2359	application.applicationId	2298
2359	application.approvalState	APPROVED
2359	application.name	949 Org
2360	message	Access is denied
2360	type	org.springframework.security.access.AccessDeniedException
2361	message	Access is denied
2361	type	org.springframework.security.access.AccessDeniedException
2362	remoteAddress	127.0.0.1
2364	message	Access is denied
2363	message	Access is denied
2364	type	org.springframework.security.access.AccessDeniedException
2363	type	org.springframework.security.access.AccessDeniedException
2365	message	Access is denied
2365	type	org.springframework.security.access.AccessDeniedException
2366	message	Access is denied
2366	type	org.springframework.security.access.AccessDeniedException
2371	affiliate.creator	able950@mailinator.com
2372	message	Access is denied
2372	type	org.springframework.security.access.AccessDeniedException
2373	message	Access is denied
2373	type	org.springframework.security.access.AccessDeniedException
2376	application.applicationId	2369
2376	application.approvalState	SUBMITTED
2376	application.name	
2377	message	Access is denied
2377	type	org.springframework.security.access.AccessDeniedException
2378	message	Access is denied
2378	type	org.springframework.security.access.AccessDeniedException
2379	message	Access is denied
2379	type	org.springframework.security.access.AccessDeniedException
2380	remoteAddress	127.0.0.1
2381	application.applicationId	2369
2381	application.approvalState	REJECTED
2381	application.name	
2382	message	Access is denied
2382	type	org.springframework.security.access.AccessDeniedException
2383	message	Access is denied
2383	type	org.springframework.security.access.AccessDeniedException
2384	remoteAddress	127.0.0.1
2386	message	An Authentication object was not found in the SecurityContext
2386	type	org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
2387	message	Access is denied
2387	type	org.springframework.security.access.AccessDeniedException
2388	remoteAddress	127.0.0.1
2391	message	Access is denied
2391	type	org.springframework.security.access.AccessDeniedException
2392	message	Access is denied
2392	type	org.springframework.security.access.AccessDeniedException
2393	remoteAddress	127.0.0.1
2394	message	Access is denied
2394	type	org.springframework.security.access.AccessDeniedException
2395	message	Access is denied
2395	type	org.springframework.security.access.AccessDeniedException
2396	remoteAddress	127.0.0.1
2397	message	Access is denied
2397	type	org.springframework.security.access.AccessDeniedException
2398	message	Access is denied
2398	type	org.springframework.security.access.AccessDeniedException
2399	remoteAddress	127.0.0.1
2400	remoteAddress	127.0.0.1
2401	remoteAddress	127.0.0.1
2402	remoteAddress	127.0.0.1
2404	remoteAddress	127.0.0.1
2406	remoteAddress	127.0.0.1
2409	remoteAddress	127.0.0.1
2412	message	Access is denied
2412	type	org.springframework.security.access.AccessDeniedException
2413	message	Access is denied
2413	type	org.springframework.security.access.AccessDeniedException
2414	message	Access is denied
2414	type	org.springframework.security.access.AccessDeniedException
2415	message	Access is denied
2415	type	org.springframework.security.access.AccessDeniedException
2416	message	Access is denied
2416	type	org.springframework.security.access.AccessDeniedException
2417	sessionId	9EF9CB9E3A0BB300A2B1B595096830CA
2417	remoteAddress	127.0.0.1
2424	remoteAddress	127.0.0.1
2432	remoteAddress	127.0.0.1
2434	remoteAddress	127.0.0.1
2438	remoteAddress	127.0.0.1
2439	message	Access is denied
2439	type	org.springframework.security.access.AccessDeniedException
2440	message	Access is denied
2440	type	org.springframework.security.access.AccessDeniedException
2441	message	Access is denied
2441	type	org.springframework.security.access.AccessDeniedException
2442	sessionId	EE2956F822CFEEDC8D28FC22AC96594A
2442	remoteAddress	127.0.0.1
2444	remoteAddress	127.0.0.1
2446	remoteAddress	127.0.0.1
2447	remoteAddress	127.0.0.1
2455	remoteAddress	127.0.0.1
2463	remoteAddress	127.0.0.1
2471	remoteAddress	127.0.0.1
2473	remoteAddress	127.0.0.1
2474	message	Access is denied
2474	type	org.springframework.security.access.AccessDeniedException
2475	message	Access is denied
2475	type	org.springframework.security.access.AccessDeniedException
2476	message	Access is denied
2476	type	org.springframework.security.access.AccessDeniedException
2477	message	Access is denied
2477	type	org.springframework.security.access.AccessDeniedException
2478	remoteAddress	127.0.0.1
2483	message	Access is denied
2483	type	org.springframework.security.access.AccessDeniedException
2484	message	Access is denied
2484	type	org.springframework.security.access.AccessDeniedException
2485	remoteAddress	127.0.0.1
2490	remoteAddress	127.0.0.1
2493	remoteAddress	127.0.0.1
2497	remoteAddress	127.0.0.1
2507	remoteAddress	127.0.0.1
2511	remoteAddress	127.0.0.1
2514	remoteAddress	127.0.0.1
2526	remoteAddress	127.0.0.1
2537	SUCCESSFUL	true
2537	IMPORTED_AFFILIATES	2
2537	READ_RECORDS	3
2537	SOURCE_MEMBER	SE
2538	remoteAddress	127.0.0.1
2549	import.affiliates	2
2549	import.records	3
2549	import.source	SE
2550	remoteAddress	127.0.0.1
2561	import.affiliates	2
2561	import.records	3
2561	import.source	SE
2562	remoteAddress	127.0.0.1
2563	import.affiliates	-1
2563	import.records	3
2564	import.affiliates	-1
2564	import.records	3
2565	import.affiliates	-1
2565	import.records	3
2581	import.affiliates	2
2581	import.records	3
2581	import.source	SE
2587	import.affiliates	2
2587	import.records	3
2587	import.source	SE
2598	import.affiliates	2
2598	import.records	3
2598	import.source	SE
2599	message	Access is denied
2599	type	org.springframework.security.access.AccessDeniedException
2600	message	Access is denied
2600	type	org.springframework.security.access.AccessDeniedException
2601	message	Access is denied
2601	type	org.springframework.security.access.AccessDeniedException
2602	sessionId	FAF965A9D22BE7C8BE483A3F58131CB7
2602	remoteAddress	127.0.0.1
2603	message	Access is denied
2603	type	org.springframework.security.access.AccessDeniedException
2604	message	Access is denied
2604	type	org.springframework.security.access.AccessDeniedException
2605	remoteAddress	127.0.0.1
2606	remoteAddress	127.0.0.1
2633	remoteAddress	127.0.0.1
2634	message	Access is denied
2634	type	org.springframework.security.access.AccessDeniedException
2635	message	Access is denied
2635	type	org.springframework.security.access.AccessDeniedException
2636	message	Access is denied
2636	type	org.springframework.security.access.AccessDeniedException
2637	message	Access is denied
2637	type	org.springframework.security.access.AccessDeniedException
2638	remoteAddress	127.0.0.1
2639	message	Access is denied
2639	type	org.springframework.security.access.AccessDeniedException
2640	remoteAddress	127.0.0.1
2641	message	Access is denied
2641	type	org.springframework.security.access.AccessDeniedException
2642	message	Access is denied
2642	type	org.springframework.security.access.AccessDeniedException
2643	remoteAddress	127.0.0.1
2644	message	Access is denied
2644	type	org.springframework.security.access.AccessDeniedException
2645	message	Access is denied
2645	type	org.springframework.security.access.AccessDeniedException
2646	remoteAddress	127.0.0.1
2647	application.applicationId	2389
2647	application.approvalState	CHANGE_REQUESTED
2647	application.name	949 Org
2648	message	Access is denied
2648	type	org.springframework.security.access.AccessDeniedException
2649	message	Access is denied
2649	type	org.springframework.security.access.AccessDeniedException
2650	remoteAddress	127.0.0.1
2651	message	Access is denied
2651	type	org.springframework.security.access.AccessDeniedException
2652	message	Access is denied
2652	type	org.springframework.security.access.AccessDeniedException
2653	message	Access is denied
2653	type	org.springframework.security.access.AccessDeniedException
2658	affiliate.creator	able951@mailinator.com
2659	message	Access is denied
2659	type	org.springframework.security.access.AccessDeniedException
2660	message	Access is denied
2660	type	org.springframework.security.access.AccessDeniedException
2664	application.applicationId	2656
2664	application.approvalState	SUBMITTED
2664	application.name	
2665	message	Access is denied
2665	type	org.springframework.security.access.AccessDeniedException
2666	message	Access is denied
2666	type	org.springframework.security.access.AccessDeniedException
2667	remoteAddress	127.0.0.1
2668	message	Access is denied
2668	type	org.springframework.security.access.AccessDeniedException
2670	application.applicationId	2656
2670	application.approvalState	APPROVED
2670	application.name	
2671	message	Access is denied
2671	type	org.springframework.security.access.AccessDeniedException
2673	message	Access is denied
2673	type	org.springframework.security.access.AccessDeniedException
2674	remoteAddress	127.0.0.1
2672	type	org.springframework.security.access.AccessDeniedException
2675	remoteAddress	127.0.0.1
2676	remoteAddress	127.0.0.1
2677	remoteAddress	127.0.0.1
2678	message	Access is denied
2678	type	org.springframework.security.access.AccessDeniedException
2679	message	Access is denied
2679	type	org.springframework.security.access.AccessDeniedException
2680	message	Access is denied
2680	type	org.springframework.security.access.AccessDeniedException
2681	message	Access is denied
2681	type	org.springframework.security.access.AccessDeniedException
2682	remoteAddress	127.0.0.1
2683	remoteAddress	127.0.0.1
2684	remoteAddress	127.0.0.1
2685	message	Access is denied
2685	type	org.springframework.security.access.AccessDeniedException
2686	message	Access is denied
2686	type	org.springframework.security.access.AccessDeniedException
2687	message	Access is denied
2687	type	org.springframework.security.access.AccessDeniedException
2688	sessionId	4CD1EC15222D2E0F3666CF596B7DC6A9
2688	remoteAddress	127.0.0.1
2689	remoteAddress	127.0.0.1
2690	remoteAddress	127.0.0.1
2701	import.affiliates	2
2701	import.records	3
2701	import.source	SE
2712	import.affiliates	2
2712	import.records	3
2712	import.source	SE
2713	remoteAddress	127.0.0.1
2724	import.affiliates	2
2724	import.records	3
2724	import.source	SE
2724	import.result	true
2725	remoteAddress	127.0.0.1
2736	import.affiliates	2
2736	import.records	3
2736	import.errors	0
2736	import.source	SE
2736	import.success	true
2737	import.affiliates	-1
2737	import.records	3
2737	import.errors	1
2737	import.success	false
2738	remoteAddress	127.0.0.1
2739	remoteAddress	127.0.0.1
2740	remoteAddress	127.0.0.1
2741	remoteAddress	127.0.0.1
2752	import.affiliates	2
2752	import.records	3
2752	import.errors	0
2752	import.source	SE
2752	import.success	true
2753	remoteAddress	127.0.0.1
2764	import.affiliates	2
2764	import.records	3
2764	import.errors	0
2764	import.source	SE
2764	import.success	true
2765	remoteAddress	127.0.0.1
2767	remoteAddress	127.0.0.1
2769	remoteAddress	127.0.0.1
2771	remoteAddress	127.0.0.1
2774	remoteAddress	127.0.0.1
2775	remoteAddress	127.0.0.1
2776	remoteAddress	127.0.0.1
2777	remoteAddress	127.0.0.1
2778	remoteAddress	127.0.0.1
2779	remoteAddress	127.0.0.1
2781	remoteAddress	127.0.0.1
2793	import.affiliates	2
2793	import.records	3
2793	import.errors	0
2793	import.source	SE
2793	import.success	true
2804	remoteAddress	127.0.0.1
2809	remoteAddress	127.0.0.1
\.


--
-- Data for Name: t_persistent_token; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_persistent_token (series, user_login, token_value, token_date, ip_address, user_agent) FROM stdin;
vTO8uy0RGf90A/P+QSWxXw==	able926@mailinator.com	Vcwa4yDvl3NLTLY5zy9v2w==	2014-07-18	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0
0TIdDyIEP922cwfN+MztCQ==	admin	dbjVjioZ8I/5i9/B5ambdw==	2014-08-14	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36
Zl3xTh2HEUMskg8O4GzWSA==	admin	vQW4yXyZhJUlBWmbQAXnsQ==	2014-07-21	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36
cq9ku/dK/a70p7EzljTr3g==	af	L28gHAxL8YcmrMaVOOPA6Q==	2014-08-06	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36
UEAQGjYn3oTnaHVGa87VBw==	able923@mailinator.com	VwNblLPxo0w3UI+7YySEAQ==	2014-07-17	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0
jNcmT17IqqDr61dIy+rqWg==	able923@mailinator.com	jCGluNvILIpIHFD1RGYnLQ==	2014-07-17	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0
RvNXfkGl2/HEh6cC4ilW2w==	able923@mailinator.com	gP5pbwkRbeEtRzIrpLLlVQ==	2014-07-17	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0
SOzJEZTy0AHF0WG7cf8oqg==	af	wlN27BE4Z22zrD5MspxK4g==	2014-08-06	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36
RpQ+ci14EeV08CbjLuw3hg==	able931@mailinator.com	j7Dqw/eDvSZcKr+EuxHu6Q==	2014-07-21	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0
pWX4YsWj7V5GZbFw7Yem3g==	admin	UFrv4nDfArbgn+sQz2YTKg==	2014-08-11	127.0.0.1	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36
\.


--
-- Data for Name: t_user; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_user (login, password, first_name, last_name, email, activated, lang_key, activation_key, created_by, created_date, last_modified_by, last_modified_date) FROM stdin;
system	572d3b834f32347527d749bc1a41042c920682fc430febd380b4b6a0134f314fd381ce11c9a05abe	\N	System	\N	t	en	\N	system	2014-06-24 12:29:08.799332-04	\N	\N
anonymousUser	4f54479f8290dfd503b72a654faf5d70593eab443993d87a79e14e5f7cda3eb7988423aa99090c9b	Anonymous	User	\N	t	en	\N	system	2014-06-24 12:29:08.799332-04	\N	\N
admin	b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1	\N	Administrator	\N	t	en	\N	system	2014-06-24 12:29:08.799332-04	\N	\N
user	4f54479f8290dfd503b72a654faf5d70593eab443993d87a79e14e5f7cda3eb7988423aa99090c9b	\N	User	\N	t	en	\N	system	2014-06-24 12:29:08.799332-04	\N	\N
able930@mailinator.com	b8141754a7df1a6e22a84b27a0cb57dcf4181c0b2e264b9490e407f8736250c1eba8154114097756	able	930	able930@mailinator.com	t	en	\N	anonymousUser	2014-07-21 12:10:21.228-04	able930@mailinator.com	2014-07-21 12:10:41.139-04
able916@mailinator.com	506a111d8c6f8756de532d69c6ac7f163f2c9492614fb8b85afb46d97f66f7e074cc13c888e131c5	able	916	able916@mailinator.com	t	en	\N	anonymousUser	2014-06-24 12:29:38.321-04	able916@mailinator.com	2014-06-24 12:41:07.607-04
able917@mailinator.com	01b37e3dffecaab617507e6a4ba05083d24e4f77c589a36c2c492030f36131b0ff12ecdcd8ea2a76	able 917	\N	able917@mailinator.com	t	en	\N	anonymousUser	2014-06-26 15:32:34.013-04	anonymousUser	2014-06-26 15:33:46.906-04
able918@mailinator.com	9ac0331f725988217ee4a11abb851d08c6daca8a062169ae06d8617c29903f9d0fea6f180545b05f	able 918	\N	able918@mailinator.com	t	en	\N	anonymousUser	2014-06-26 15:38:43.249-04	anonymousUser	2014-06-26 15:38:55.098-04
able919@mailinator.com	eda96cbbb0c53114261f9e0aac9c5ea37291829e7ad9cf83b3150435b903d2db71db717c5259ad7c	able 919	\N	able919@mailinator.com	t	en	\N	anonymousUser	2014-06-26 16:22:09.081-04	anonymousUser	2014-06-26 16:22:19.528-04
able920@mailinator.com	274ec0740e04ad644e8017d6a50a4774195b325f56c5870870cd6e94d1151b2b26bd26b0da4315db	able 920	\N	able920@mailinator.com	t	en	\N	anonymousUser	2014-07-03 11:43:33.692-04	anonymousUser	2014-07-03 11:44:18.853-04
able921@mailinator.com	5edc2e5a97faf94c48b1fc7aa895b17dec490f5a90c1c76a1c26d56a17f231966cbaa6082d413b6e	able 921	\N	able921@mailinator.com	t	en	\N	anonymousUser	2014-07-07 13:42:10.219-04	able921@mailinator.com	2014-07-07 13:42:35.254-04
able922@mailinator.com	943e484cdc6538c89a988479fb46cb91c63174fce2fa628e275231f15e7bfbc22047c827614d0001	\N	\N	able922@mailinator.com	t	en	\N	anonymousUser	2014-07-07 14:25:06.079-04	able922@mailinator.com	2014-07-07 14:25:43.686-04
able923@mailinator.com	540d745657a9c43a8cb3a4b5271e1889f18380d249514e7db98bcb0e63880abb5d60fc92b147b523	able 923	\N	able923@mailinator.com	t	en	\N	admin	2014-07-09 12:25:57.206-04	able923@mailinator.com	2014-07-09 12:26:35.65-04
able925@mailinator.com	2204861b8795c1a42a2eb91c35a1ed06483a4798d44e20a0a254e35e80e8adc5226009e888cf70ff	able	925	able925@mailinator.com	t	en	\N	anonymousUser	2014-07-18 13:13:25.605-04	able925@mailinator.com	2014-07-18 13:13:38.514-04
able926@mailinator.com	6f853a17dd17fddaeb09f5a8939a14a2ad1fc926b3eb92e16a942483cdba70e23d60f9945726ad9c	able	926	able926@mailinator.com	t	en	\N	anonymousUser	2014-07-18 15:40:44.239-04	able926@mailinator.com	2014-07-18 15:41:01.069-04
able927@mailinator.com	518cd7ba311889a7d4b908f92884215ea8c368283846908a3d306bfa6fe0745421f5ac93112471f6	able	927	able927@mailinator.com	t	en	\N	anonymousUser	2014-07-18 15:51:36.302-04	able927@mailinator.com	2014-07-18 15:51:51.751-04
able928@mailinator.com	30061fd60edf84dc3618332ede51a2d31e48245d5391a6eaf88d1c6136f139d15d532b9a37181cd3	able	928	able928@mailinator.com	t	en	\N	anonymousUser	2014-07-21 11:45:46.968-04	able928@mailinator.com	2014-07-21 11:46:04.929-04
able929@mailinator.com	0039dce28bf911aed8400fc30c820e3b02b0b8b324a81fa3578cb295a33cbd3a0e35bb7e58c8d02e	able	929	able929@mailinator.com	t	en	\N	anonymousUser	2014-07-21 11:53:24.384-04	able929@mailinator.com	2014-07-21 11:53:39.567-04
able931@mailinator.com	ac4be9064380e5197cb77de6389c2f55ad15e23c5cf28ed178e804c88e29a0f2721b0ca41a3e7ba6	able	931	able931@mailinator.com	t	en	03994809665320757147	anonymousUser	2014-07-21 12:24:24.209-04	able931@mailinator.com	2014-07-21 12:24:48.524-04
able931@intelliware.ca	e9c75c32dd155afc9338a3c48257a269d3b44c39217235a7846857b570b39692b6bf434382b110c9	able	931	able931@intelliware.ca	t	en	91393681630844704319	anonymousUser	2014-07-21 16:22:38.1-04	able931@intelliware.ca	2014-07-21 16:22:51.152-04
able932@mailinator.com	c8660123141dfa9865a8dcf91ddc4739704be39f2cdd2d1d8e340ae9be1d8caa3508221e9cee2656	able	932	able932@mailinator.com	t	en	34673657259480909926	anonymousUser	2014-07-21 17:02:11.746-04	able932@mailinator.com	2014-07-21 17:02:27.706-04
able933@mailinator.com	30c50227c2efd0af621631153b26eb510c9ca16e5607e66a9e33606f5383e425cad91c641c8e0166	able	933	able933@mailinator.com	t	en	76028037477384463145	anonymousUser	2014-07-23 12:28:34.461-04	able933@mailinator.com	2014-07-23 12:29:04.293-04
able934@mailinator.com	05833fdb166edcab50181cb6ababf5431f6f345c1db19141d5e317d27ec96a09d0f9280001c886bf	able	934	able934@mailinator.com	t	en	36906997519438517699	anonymousUser	2014-07-23 16:30:44.555-04	able934@mailinator.com	2014-07-23 16:30:56.887-04
staff	edca6651ee089a1e0b9320988f72e62dc5339f90dcc6dbbfd82a3127d2727610b52d2b0743d9b7ff	\N	\N	\N	t	\N	\N	system	2014-07-25 10:27:30.328266-04	\N	\N
sweden	e6b4dc6acaa889021ab202e417245c0880c66b254ad5758a298d310d15d38f1385bc0d82fc6f5e62	\N	\N	\N	t	\N	\N	system	2014-07-25 10:27:30.328266-04	\N	\N
able935@mailinator.com	c20ba0393d939c4d6e7b84498cbf3f2adc5bd341513f04d6470e2f81aacd8292089236df332fe4af	able	935	able935@mailinator.com	t	en	33881044950246610886	anonymousUser	2014-07-25 10:39:07.144-04	able935@mailinator.com	2014-07-25 10:39:21.176-04
able936@mailinator.com	aca652b6fd2f4f1395387b6d61aa0d473bbefa925265326c5105f4a4c96de3c8fef0f2f4bf4fcca1	able	936	able936@mailinator.com	t	en	64324369375915116715	anonymousUser	2014-07-28 11:26:30.073-04	able936@mailinator.com	2014-07-28 11:26:48.98-04
able938@mailinator.com	873a3a91ba024f77a98979e785f66fd2d93ef362e9ecfddd311327187dce7a500a414b429cd659cb	able	938	able938@mailinator.com	t	en	48909287459716426487	anonymousUser	2014-07-29 15:48:40.952-04	able938@mailinator.com	2014-07-29 15:49:18.44-04
able937@mailinator.com	25bd3cc6d1a8ba4c38a74d963ecbe9a296b6760aafa2b25ea45bc0bb85b43c01f6b6254381388ffb	able	937	able937@mailinator.com	t	en	03684495744320171896	anonymousUser	2014-07-28 16:21:45.86-04	able937@mailinator.com	2014-07-28 17:02:20.713-04
able939@mailinator.com	a3b09d935b6948d90dd729f2fe8a278364cd63dc847e1cc6ec6c8d4a881f8bfc423ee105f56ec924	able	939	able939@mailinator.com	t	en	80168781385945059011	anonymousUser	2014-07-31 12:03:47.457-04	able939@mailinator.com	2014-07-31 12:04:40.069-04
able940@mailinator.com	93061db5b730db184be1c3e7d07b0b5fa9c7307a04f2f5da3c2e509fe0fb44f4c867f687d64b9998	able	940	able940@mailinator.com	t	en	75430963618960579025	anonymousUser	2014-07-31 15:16:42.515-04	able940@mailinator.com	2014-07-31 15:16:57.982-04
able941@mailinator.com	33e54f8fb645e06f3abedf29ae6cf3ec31ee6ddb6218dd4e8e1bd6372c191c67eb84833ea3d5f7a0	able	941	able941@mailinator.com	t	en	14144473953204291035	anonymousUser	2014-07-31 15:32:51.414-04	able941@mailinator.com	2014-07-31 15:33:02.309-04
able942@mailinator.com	1890f60768fcac31c9dc6f8f2ebf363aa20cb6bf27a6c056beeec3b2ed08756958a26d98481302ec	able	942	able942@mailinator.com	t	en	00692576571530379150	anonymousUser	2014-07-31 15:35:39.982-04	able942@mailinator.com	2014-07-31 15:35:50.473-04
able943@mailinator.com	5201e0ce6d65342ddee2cb9dcede7b9b4785a95fac8c937d8132392bb98878514b02620f7c35fc42	able	943	able943@mailinator.com	f	en	82500596907881580574	anonymousUser	2014-08-05 13:57:02.547-04	anonymousUser	2014-08-05 13:57:02.547-04
able944@mailinator.com	e5672911f55c108b857b32ea1eccff3a13d31130759d2d5c74db8ae3a25c27000633ed02cca9b3d2	able	944	able944@mailinator.com	t	en	78769701027801330786	anonymousUser	2014-08-05 14:32:03.24-04	able944@mailinator.com	2014-08-05 14:32:21.32-04
me@bad1.com	e315ca474490d320ac3fafd92c824a46865d34f8638fe731525ad5d66d820dccb4c6cfa05c56f7c4	bad	bad	me@bad1.com	f	en	34129664976318580189	anonymousUser	2014-08-05 14:43:46.666-04	anonymousUser	2014-08-05 14:43:46.666-04
able945@mailinator.com	96b470fb0922fe2f5c381b06942d13f5449fbcae7e29204afb0dcb6ad2b561359717277893d5c62d	able	945	able945@mailinator.com	t	en	71619811002853269630	anonymousUser	2014-08-05 16:18:05.917-04	able945@mailinator.com	2014-08-05 16:18:20.215-04
able946@mailinator.com	2ac18061dba5fdbeda755ae7190d2f7825070022bb4c7630b0831ce10470b3a78710190ee8331eac	able	946	able946@mailinator.com	t	en	83952605889516400273	anonymousUser	2014-08-05 17:11:50.331-04	able946@mailinator.com	2014-08-05 17:12:03.28-04
able948@mailinator.com	a6331bf6d7491a200fa303d831a95a55d253f7ce662ae3c81b96a13a020f34eaea61449117e38ea3	able	948	able948@mailinator.com	t	en	72238575765907938546	anonymousUser	2014-08-06 10:36:52.626-04	able948@mailinator.com	2014-08-06 10:37:08.657-04
able947@mailinator.com	aea3ca96972aa03eac597cc10118021c210b90cf3f4c59bae57ce074283e3123606c0615779ef970	able	947	able947@mailinator.com	t	en	43774871706093729938	anonymousUser	2014-08-06 10:01:10.924-04	able947@mailinator.com	2014-08-06 13:18:25.244-04
able949@mailinator.com	82c10d1e7d98120b2552be7bc4e15ea64a8c49f789794481e985d39cf5c71938de1b9b5e867b3622	able	949	able949@mailinator.com	t	en	84108879623316401939	anonymousUser	2014-08-06 13:19:32.13-04	able949@mailinator.com	2014-08-06 13:19:47.315-04
af	edca6651ee089a1e0b9320988f72e62dc5339f90dcc6dbbfd82a3127d2727610b52d2b0743d9b7ff	\N	\N	\N	t	\N	\N	system	2014-08-06 16:12:19.307629-04	\N	\N
fr	edca6651ee089a1e0b9320988f72e62dc5339f90dcc6dbbfd82a3127d2727610b52d2b0743d9b7ff	\N	\N	\N	t	\N	\N	system	2014-08-06 16:12:28.106764-04	\N	\N
zw	edca6651ee089a1e0b9320988f72e62dc5339f90dcc6dbbfd82a3127d2727610b52d2b0743d9b7ff	\N	\N	\N	t	\N	\N	system	2014-08-06 16:12:34.746671-04	\N	\N
able950@mailinator.com	b7d6e912dff3fae414013f5023d64504dc53ff327e26c23dc3fe5ee27de1d85d99d80660ec8f4ac1	able	950	able950@mailinator.com	t	en	87088579609186205157	anonymousUser	2014-08-08 10:22:38.728-04	able950@mailinator.com	2014-08-08 10:22:48.403-04
able951@mailinator.com	d4125664775a6eb6e994548b73e70aa89e73006f855758beac4caba6e5f5afac2116e8953004f0c2	able	951	able951@mailinator.com	t	en	36994642850414342533	anonymousUser	2014-08-12 15:19:04.94-04	able951@mailinator.com	2014-08-12 15:19:18.437-04
\.


--
-- Data for Name: t_user_authority; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY t_user_authority (login, name) FROM stdin;
admin	ROLE_ADMIN
admin	ROLE_USER
user	ROLE_USER
system	ROLE_ADMIN
system	ROLE_USER
able916@mailinator.com	ROLE_USER
able917@mailinator.com	ROLE_USER
able918@mailinator.com	ROLE_USER
able919@mailinator.com	ROLE_USER
able920@mailinator.com	ROLE_USER
able921@mailinator.com	ROLE_USER
able922@mailinator.com	ROLE_USER
able923@mailinator.com	ROLE_USER
able925@mailinator.com	ROLE_USER
able926@mailinator.com	ROLE_USER
able927@mailinator.com	ROLE_USER
able928@mailinator.com	ROLE_USER
able929@mailinator.com	ROLE_USER
able930@mailinator.com	ROLE_USER
able931@mailinator.com	ROLE_USER
able931@intelliware.ca	ROLE_USER
able932@mailinator.com	ROLE_USER
able933@mailinator.com	ROLE_USER
able934@mailinator.com	ROLE_USER
staff	ROLE_STAFF
staff	ROLE_STAFF_IHTSDO
sweden	ROLE_STAFF
sweden	ROLE_STAFF_SE
able935@mailinator.com	ROLE_USER
able936@mailinator.com	ROLE_USER
able937@mailinator.com	ROLE_USER
able938@mailinator.com	ROLE_USER
able939@mailinator.com	ROLE_USER
able940@mailinator.com	ROLE_USER
able941@mailinator.com	ROLE_USER
able942@mailinator.com	ROLE_USER
able943@mailinator.com	ROLE_USER
able944@mailinator.com	ROLE_USER
me@bad1.com	ROLE_USER
able945@mailinator.com	ROLE_USER
able946@mailinator.com	ROLE_USER
able947@mailinator.com	ROLE_USER
able948@mailinator.com	ROLE_USER
able949@mailinator.com	ROLE_USER
af	ROLE_STAFF
fr	ROLE_STAFF
zw	ROLE_STAFF
af	ROLE_STAFF_AF
fr	ROLE_STAFF_FR
zw	ROLE_STAFF_ZW
able950@mailinator.com	ROLE_USER
able951@mailinator.com	ROLE_USER
\.


--
-- Data for Name: user_registration; Type: TABLE DATA; Schema: public; Owner: mlds
--

COPY user_registration (user_registration_id, email) FROM stdin;
\.


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
-- Name: pk_t_user; Type: CONSTRAINT; Schema: public; Owner: mlds; Tablespace: 
--

ALTER TABLE ONLY t_user
    ADD CONSTRAINT pk_t_user PRIMARY KEY (login);


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
    ADD CONSTRAINT t_user_authority_pkey PRIMARY KEY (login, name);


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
-- Name: idx_user_authority; Type: INDEX; Schema: public; Owner: mlds; Tablespace: 
--

CREATE UNIQUE INDEX idx_user_authority ON t_user_authority USING btree (login, name);


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
-- Name: FK_package_t_user; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY release_package
    ADD CONSTRAINT "FK_package_t_user" FOREIGN KEY (created_by) REFERENCES t_user(login);


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
-- Name: FK_release_version_t_user; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY release_version
    ADD CONSTRAINT "FK_release_version_t_user" FOREIGN KEY (created_by) REFERENCES t_user(login);


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
-- Name: fk_user_login; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_user_authority
    ADD CONSTRAINT fk_user_login FOREIGN KEY (login) REFERENCES t_user(login);


--
-- Name: fk_user_persistent_token; Type: FK CONSTRAINT; Schema: public; Owner: mlds
--

ALTER TABLE ONLY t_persistent_token
    ADD CONSTRAINT fk_user_persistent_token FOREIGN KEY (user_login) REFERENCES t_user(login);


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

