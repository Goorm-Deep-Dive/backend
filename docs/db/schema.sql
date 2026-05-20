-- DROP SCHEMA ending_schema;

CREATE SCHEMA ending_schema AUTHORIZATION ending;

-- DROP TYPE ending_schema."channel_type_enum";

CREATE TYPE ending_schema."channel_type_enum" AS ENUM (
	'ONLINE',
	'VISIT',
	'POSTAL',
	'PHONE',
	'FAX');

-- DROP TYPE ending_schema."chat_role_enum";

CREATE TYPE ending_schema."chat_role_enum" AS ENUM (
	'USER',
	'AI');

-- DROP TYPE ending_schema."document_channel_type_enum";

CREATE TYPE ending_schema."document_channel_type_enum" AS ENUM (
	'ONLINE',
	'OFFLINE',
	'BOTH');

-- DROP TYPE ending_schema."document_type_enum";

CREATE TYPE ending_schema."document_type_enum" AS ENUM (
	'REQUIRED',
	'OPTIONAL',
	'CONDITIONAL');

-- DROP TYPE ending_schema."due_date_type_enum";

CREATE TYPE ending_schema."due_date_type_enum" AS ENUM (
	'IMMEDIATE',
	'RELATIVE',
	'DEATH_END_DAY',
	'DEATH_MONTH',
	'NONE');

-- DROP TYPE ending_schema."due_date_unit_enum";

CREATE TYPE ending_schema."due_date_unit_enum" AS ENUM (
	'YEAR',
	'MONTH',
	'DAY');

-- DROP TYPE ending_schema."notification_delivery_status";

CREATE TYPE ending_schema."notification_delivery_status" AS ENUM (
	'PENDING',
	'SENT',
	'FAILED');

-- DROP TYPE ending_schema."provider_enum";

CREATE TYPE ending_schema."provider_enum" AS ENUM (
	'GOOGLE',
	'KAKAO',
	'NAVER');

-- DROP TYPE ending_schema."role_enum";

CREATE TYPE ending_schema."role_enum" AS ENUM (
	'USER',
	'ADMIN');

-- DROP TYPE ending_schema."survey_answer_type";

CREATE TYPE ending_schema."survey_answer_type" AS ENUM (
	'NORMAL',
	'UNKNOWN',
	'NOT_APPLICABLE');

-- DROP TYPE ending_schema."survey_question_type_enum";

CREATE TYPE ending_schema."survey_question_type_enum" AS ENUM (
	'SINGLE',
	'MULTIPLE');

-- DROP TYPE ending_schema."survey_requirement_type_enum";

CREATE TYPE ending_schema."survey_requirement_type_enum" AS ENUM (
	'REQUIRED',
	'OPTIONAL');

-- DROP TYPE ending_schema."survey_status_enum";

CREATE TYPE ending_schema."survey_status_enum" AS ENUM (
	'NOT_STARTED',
	'IN_PROGRESS',
	'COMPLETED',
	'SKIPPED');

-- DROP SEQUENCE ending_schema.calendar_events_calendar_event_id_seq;

CREATE SEQUENCE ending_schema.calendar_events_calendar_event_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.chat_messages_chat_message_id_seq;

CREATE SEQUENCE ending_schema.chat_messages_chat_message_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.deceased_profiles_deceased_profile_id_seq;

CREATE SEQUENCE ending_schema.deceased_profiles_deceased_profile_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.notifications_notification_id_seq;

CREATE SEQUENCE ending_schema.notifications_notification_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.procedure_categories_procedure_category_id_seq;

CREATE SEQUENCE ending_schema.procedure_categories_procedure_category_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.procedure_channels_procedure_channel_id_seq;

CREATE SEQUENCE ending_schema.procedure_channels_procedure_channel_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.procedure_contacts_procedure_contact_id_seq;

CREATE SEQUENCE ending_schema.procedure_contacts_procedure_contact_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.procedure_documents_procedure_document_id_seq;

CREATE SEQUENCE ending_schema.procedure_documents_procedure_document_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.procedures_procedure_id_seq;

CREATE SEQUENCE ending_schema.procedures_procedure_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.refresh_tokens_refresh_token_id_seq;

CREATE SEQUENCE ending_schema.refresh_tokens_refresh_token_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.survey_answer_procedures_survey_answer_procedure_id_seq;

CREATE SEQUENCE ending_schema.survey_answer_procedures_survey_answer_procedure_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.survey_answers_survey_answer_id_seq;

CREATE SEQUENCE ending_schema.survey_answers_survey_answer_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.survey_questions_survey_question_id_seq;

CREATE SEQUENCE ending_schema.survey_questions_survey_question_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.survey_responses_survey_response_id_seq;

CREATE SEQUENCE ending_schema.survey_responses_survey_response_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.user_document_checklists_user_document_checklist_id_seq;

CREATE SEQUENCE ending_schema.user_document_checklists_user_document_checklist_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.user_event_logs_user_event_log_id_seq;

CREATE SEQUENCE ending_schema.user_event_logs_user_event_log_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.user_procedure_checklists_user_procedure_checklist_id_seq;

CREATE SEQUENCE ending_schema.user_procedure_checklists_user_procedure_checklist_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE ending_schema.users_user_id_seq;

CREATE SEQUENCE ending_schema.users_user_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;-- ending_schema.procedure_categories definition

-- Drop table

-- DROP TABLE ending_schema.procedure_categories;

CREATE TABLE ending_schema.procedure_categories (
                                                    procedure_category_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                    category_name varchar(100) NOT NULL,
                                                    description varchar(255) NULL,
                                                    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                    color varchar(20) NULL,
                                                    icon varchar(100) NULL,
                                                    CONSTRAINT procedure_categories_pkey PRIMARY KEY (procedure_category_id)
);


-- ending_schema.survey_questions definition

-- Drop table

-- DROP TABLE ending_schema.survey_questions;

CREATE TABLE ending_schema.survey_questions (
                                                survey_question_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                question_type ending_schema."survey_question_type_enum" NULL,
                                                requirement_type ending_schema."survey_requirement_type_enum" NULL,
                                                survey_question_text varchar(200) NOT NULL,
                                                created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                description varchar(500) NULL,
                                                CONSTRAINT survey_questions_pkey PRIMARY KEY (survey_question_id)
);


-- ending_schema.vector_store definition

-- Drop table

-- DROP TABLE ending_schema.vector_store;

CREATE TABLE ending_schema.vector_store (
                                            id uuid DEFAULT gen_random_uuid() NOT NULL,
                                            "content" text NULL,
                                            metadata json NULL,
                                            embedding public.vector NULL,
                                            CONSTRAINT vector_store_pkey PRIMARY KEY (id)
);
CREATE INDEX vector_store_embedding_idx ON ending_schema.vector_store USING hnsw (embedding vector_cosine_ops);


-- ending_schema."procedures" definition

-- Drop table

-- DROP TABLE ending_schema."procedures";

CREATE TABLE ending_schema."procedures" (
                                            procedure_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                            procedure_category_id int8 NOT NULL,
                                            procedure_name varchar(100) NOT NULL,
                                            description varchar(255) NULL,
                                            due_date_type ending_schema."due_date_type_enum" NULL,
                                            due_date_unit ending_schema."due_date_unit_enum" NULL,
                                            due_date int4 NULL,
                                            due_date_description varchar(200) NULL,
                                            search_scope varchar(500) NULL,
                                            caution_text text NULL,
                                            created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            priority int4 DEFAULT 1 NOT NULL,
                                            CONSTRAINT procedures_pkey PRIMARY KEY (procedure_id),
                                            CONSTRAINT fk_procedures_category FOREIGN KEY (procedure_category_id) REFERENCES ending_schema.procedure_categories(procedure_category_id)
);
CREATE INDEX idx_procedures_category_id ON ending_schema.procedures USING btree (procedure_category_id);


-- ending_schema.survey_answers definition

-- Drop table

-- DROP TABLE ending_schema.survey_answers;

CREATE TABLE ending_schema.survey_answers (
                                              survey_answer_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                              survey_question_id int8 NOT NULL,
                                              next_question_id int8 NULL,
                                              survey_answer_text varchar(200) NOT NULL,
                                              created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                              updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                              answer_type ending_schema."survey_answer_type" DEFAULT 'NORMAL'::ending_schema.survey_answer_type NOT NULL,
                                              CONSTRAINT survey_answers_pkey PRIMARY KEY (survey_answer_id),
                                              CONSTRAINT fk_survey_answers_next_question FOREIGN KEY (next_question_id) REFERENCES ending_schema.survey_questions(survey_question_id) ON UPDATE CASCADE,
                                              CONSTRAINT fk_survey_answers_question FOREIGN KEY (survey_question_id) REFERENCES ending_schema.survey_questions(survey_question_id) ON UPDATE CASCADE
);
CREATE INDEX idx_survey_answers_next_question_id ON ending_schema.survey_answers USING btree (next_question_id);
CREATE INDEX idx_survey_answers_question_id ON ending_schema.survey_answers USING btree (survey_question_id);


-- ending_schema.procedure_channels definition

-- Drop table

-- DROP TABLE ending_schema.procedure_channels;

CREATE TABLE ending_schema.procedure_channels (
                                                  procedure_channel_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                  procedure_id int8 NOT NULL,
                                                  channel_type ending_schema."channel_type_enum" NULL,
                                                  description varchar(255) NULL,
                                                  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                  CONSTRAINT procedure_channels_pkey PRIMARY KEY (procedure_channel_id),
                                                  CONSTRAINT fk_procedure_channels_procedure FOREIGN KEY (procedure_id) REFERENCES ending_schema."procedures"(procedure_id) ON DELETE CASCADE
);
CREATE INDEX idx_procedure_channels_procedure_id ON ending_schema.procedure_channels USING btree (procedure_id);


-- ending_schema.procedure_contacts definition

-- Drop table

-- DROP TABLE ending_schema.procedure_contacts;

CREATE TABLE ending_schema.procedure_contacts (
                                                  procedure_contact_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                  procedure_id int8 NOT NULL,
                                                  title varchar(100) NULL,
                                                  description varchar(255) NULL,
                                                  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                  updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                  CONSTRAINT procedure_contacts_pkey PRIMARY KEY (procedure_contact_id),
                                                  CONSTRAINT fk_procedure_contacts_procedure FOREIGN KEY (procedure_id) REFERENCES ending_schema."procedures"(procedure_id) ON DELETE CASCADE
);
CREATE INDEX idx_procedure_contacts_procedure_id ON ending_schema.procedure_contacts USING btree (procedure_id);


-- ending_schema.procedure_documents definition

-- Drop table

-- DROP TABLE ending_schema.procedure_documents;

CREATE TABLE ending_schema.procedure_documents (
                                                   procedure_document_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                   procedure_id int8 NOT NULL,
                                                   document_type ending_schema."document_type_enum" NOT NULL,
                                                   document_channel_type ending_schema."document_channel_type_enum" NOT NULL,
                                                   document_name varchar(200) NOT NULL,
                                                   document_location varchar(255) NULL,
                                                   document_link varchar(255) NULL,
                                                   description text NULL,
                                                   created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                   updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                   CONSTRAINT procedure_documents_pkey PRIMARY KEY (procedure_document_id),
                                                   CONSTRAINT fk_procedure_documents_procedure FOREIGN KEY (procedure_id) REFERENCES ending_schema."procedures"(procedure_id) ON DELETE CASCADE
);
CREATE INDEX idx_procedure_documents_procedure_id ON ending_schema.procedure_documents USING btree (procedure_id);


-- ending_schema.survey_answer_procedures definition

-- Drop table

-- DROP TABLE ending_schema.survey_answer_procedures;

CREATE TABLE ending_schema.survey_answer_procedures (
                                                        survey_answer_procedure_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                        survey_answer_id int8 NOT NULL,
                                                        procedure_id int8 NOT NULL,
                                                        created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                        CONSTRAINT survey_answer_procedures_pkey PRIMARY KEY (survey_answer_procedure_id),
                                                        CONSTRAINT uq_survey_answer_procedures UNIQUE (survey_answer_id, procedure_id),
                                                        CONSTRAINT fk_survey_answer_procedures_answer FOREIGN KEY (survey_answer_id) REFERENCES ending_schema.survey_answers(survey_answer_id) ON DELETE CASCADE,
                                                        CONSTRAINT fk_survey_answer_procedures_procedure FOREIGN KEY (procedure_id) REFERENCES ending_schema."procedures"(procedure_id) ON DELETE CASCADE
);
CREATE INDEX idx_survey_answer_procedures_answer_id ON ending_schema.survey_answer_procedures USING btree (survey_answer_id);
CREATE INDEX idx_survey_answer_procedures_procedure_id ON ending_schema.survey_answer_procedures USING btree (procedure_id);


-- ending_schema.calendar_events definition

-- Drop table

-- DROP TABLE ending_schema.calendar_events;

CREATE TABLE ending_schema.calendar_events (
                                               calendar_event_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                               user_id int8 NOT NULL,
                                               title varchar(200) NOT NULL,
                                               description text NULL,
                                               start_at timestamp NOT NULL,
                                               end_at timestamp NULL,
                                               google_calendar_id varchar(255) NULL,
                                               google_event_id varchar(255) NULL,
                                               created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                               updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                               user_procedure_checklist_id int8 NULL,
                                               event_type varchar(20) DEFAULT 'CHECKLIST'::character varying NOT NULL,
                                               sync_status varchar(20) DEFAULT 'SYNCED'::character varying NOT NULL,
                                               last_synced_at timestamp NULL,
                                               deceased_profile_id int8 NULL,
                                               CONSTRAINT calendar_events_pkey PRIMARY KEY (calendar_event_id)
);
CREATE INDEX idx_calendar_events_deceased_profile_id ON ending_schema.calendar_events USING btree (deceased_profile_id);
CREATE INDEX idx_calendar_events_user_id ON ending_schema.calendar_events USING btree (user_id);
CREATE UNIQUE INDEX uq_calendar_checklist ON ending_schema.calendar_events USING btree (user_procedure_checklist_id) WHERE (user_procedure_checklist_id IS NOT NULL);
CREATE UNIQUE INDEX uq_google_event ON ending_schema.calendar_events USING btree (google_event_id) WHERE (google_event_id IS NOT NULL);


-- ending_schema.chat_messages definition

-- Drop table

-- DROP TABLE ending_schema.chat_messages;

CREATE TABLE ending_schema.chat_messages (
                                             chat_message_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                             user_id int8 NOT NULL,
                                             "content" text NULL,
                                             "role" ending_schema."chat_role_enum" NULL,
                                             created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                             CONSTRAINT chat_messages_pkey PRIMARY KEY (chat_message_id)
);
CREATE INDEX idx_chat_messages_user_id ON ending_schema.chat_messages USING btree (user_id);
CREATE INDEX idx_chat_messages_user_id_created_at ON ending_schema.chat_messages USING btree (user_id, created_at);


-- ending_schema.deceased_profiles definition

-- Drop table

-- DROP TABLE ending_schema.deceased_profiles;

CREATE TABLE ending_schema.deceased_profiles (
                                                 deceased_profile_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                 user_id int8 NOT NULL,
                                                 date_of_death date NOT NULL,
                                                 "survey_status" ending_schema."survey_status_enum" DEFAULT 'NOT_STARTED'::ending_schema.survey_status_enum NULL,
                                                 created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                 updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                 "name" varchar(50) NOT NULL,
                                                 CONSTRAINT deceased_profiles_pkey PRIMARY KEY (deceased_profile_id)
);


-- ending_schema.notifications definition

-- Drop table

-- DROP TABLE ending_schema.notifications;

CREATE TABLE ending_schema.notifications (
                                             notification_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                             user_id int8 NOT NULL,
                                             user_procedure_checklist_id int8 NOT NULL,
                                             message varchar(500) NULL,
                                             is_read bool DEFAULT false NOT NULL,
                                             created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                             updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                             idempotency_key varchar(36) NOT NULL,
                                             delivery_status ending_schema."notification_delivery_status" DEFAULT 'PENDING'::ending_schema.notification_delivery_status NOT NULL,
                                             sent_at timestamp NULL,
                                             failure_reason varchar(500) NULL,
                                             deceased_profile_id int8 NOT NULL,
                                             CONSTRAINT notifications_pkey PRIMARY KEY (notification_id),
                                             CONSTRAINT uq_notifications_idempotency_key UNIQUE (idempotency_key)
);
CREATE INDEX idx_notifications_user_id ON ending_schema.notifications USING btree (user_id);
CREATE INDEX idx_notifications_user_procedure_checklist_id ON ending_schema.notifications USING btree (user_procedure_checklist_id);


-- ending_schema.refresh_tokens definition

-- Drop table

-- DROP TABLE ending_schema.refresh_tokens;

CREATE TABLE ending_schema.refresh_tokens (
                                              refresh_token_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                              user_id int8 NOT NULL,
                                              refresh_token text NOT NULL,
                                              expires_at timestamp NOT NULL,
                                              created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                              CONSTRAINT refresh_tokens_pkey PRIMARY KEY (refresh_token_id)
);
CREATE INDEX idx_refresh_tokens_user_id ON ending_schema.refresh_tokens USING btree (user_id);


-- ending_schema.survey_responses definition

-- Drop table

-- DROP TABLE ending_schema.survey_responses;

CREATE TABLE ending_schema.survey_responses (
                                                survey_response_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                deceased_profile_id int8 NOT NULL,
                                                survey_answer_id int8 NOT NULL,
                                                created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                CONSTRAINT survey_responses_pkey PRIMARY KEY (survey_response_id)
);
CREATE INDEX idx_survey_responses_deceased_profile_id ON ending_schema.survey_responses USING btree (deceased_profile_id);
CREATE INDEX idx_survey_responses_survey_answer_id ON ending_schema.survey_responses USING btree (survey_answer_id);


-- ending_schema.user_document_checklists definition

-- Drop table

-- DROP TABLE ending_schema.user_document_checklists;

CREATE TABLE ending_schema.user_document_checklists (
                                                        user_document_checklist_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                        deceased_profile_id int8 NOT NULL,
                                                        procedure_document_id int8 NOT NULL,
                                                        is_checked bool DEFAULT false NOT NULL,
                                                        created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                        CONSTRAINT uk_profile_document UNIQUE (deceased_profile_id, procedure_document_id),
                                                        CONSTRAINT user_document_checklists_pkey PRIMARY KEY (user_document_checklist_id)
);
CREATE INDEX idx_user_document_checklists_deceased_profile_id ON ending_schema.user_document_checklists USING btree (deceased_profile_id);
CREATE INDEX idx_user_document_checklists_procedure_document_id ON ending_schema.user_document_checklists USING btree (procedure_document_id);


-- ending_schema.user_event_logs definition

-- Drop table

-- DROP TABLE ending_schema.user_event_logs;

CREATE TABLE ending_schema.user_event_logs (
                                               user_event_log_id bigserial NOT NULL,
                                               device_id varchar(255) NULL,
                                               user_id int8 NULL,
                                               event_type varchar(50) NOT NULL,
                                               provider varchar(30) NULL,
                                               failure_reason varchar(255) NULL,
                                               created_at timestamp DEFAULT now() NOT NULL,
                                               CONSTRAINT user_event_logs_pkey PRIMARY KEY (user_event_log_id)
);
CREATE INDEX idx_user_event_logs_device_id ON ending_schema.user_event_logs USING btree (device_id);
CREATE INDEX idx_user_event_logs_user_id ON ending_schema.user_event_logs USING btree (user_id);


-- ending_schema.user_procedure_checklists definition

-- Drop table

-- DROP TABLE ending_schema.user_procedure_checklists;

CREATE TABLE ending_schema.user_procedure_checklists (
                                                         user_procedure_checklist_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                                         deceased_profile_id int8 NOT NULL,
                                                         procedure_id int8 NOT NULL,
                                                         is_checked bool DEFAULT false NOT NULL,
                                                         due_date timestamp NULL,
                                                         created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                         updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                         CONSTRAINT uk_profile_procedure UNIQUE (deceased_profile_id, procedure_id),
                                                         CONSTRAINT user_procedure_checklists_pkey PRIMARY KEY (user_procedure_checklist_id)
);
CREATE INDEX idx_user_procedure_checklists_deceased_profile_id ON ending_schema.user_procedure_checklists USING btree (deceased_profile_id);
CREATE INDEX idx_user_procedure_checklists_procedure_id ON ending_schema.user_procedure_checklists USING btree (procedure_id);


-- ending_schema.users definition

-- Drop table

-- DROP TABLE ending_schema.users;

CREATE TABLE ending_schema.users (
                                     user_id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
                                     provider ending_schema."provider_enum" NOT NULL,
                                     provider_user_id varchar(100) NULL,
                                     "name" varchar(50) NULL,
                                     email varchar(100) NULL,
                                     "role" ending_schema."role_enum" NOT NULL,
                                     is_notification_enabled bool DEFAULT true NOT NULL,
                                     provider_access_token text NULL,
                                     provider_refresh_token text NULL,
                                     google_provider_user_id varchar(100) NULL,
                                     created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     google_access_token text NULL,
                                     google_refresh_token text NULL,
                                     active_deceased_profile_id int8 NULL,
                                     fcm_token text NULL,
                                     CONSTRAINT uq_users_provider_provider_user_id UNIQUE (provider, provider_user_id),
                                     CONSTRAINT users_pkey PRIMARY KEY (user_id)
);


-- ending_schema.calendar_events foreign keys

ALTER TABLE ending_schema.calendar_events ADD CONSTRAINT fk_calendar_events_checklist FOREIGN KEY (user_procedure_checklist_id) REFERENCES ending_schema.user_procedure_checklists(user_procedure_checklist_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.calendar_events ADD CONSTRAINT fk_calendar_events_deceased_profile FOREIGN KEY (deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.calendar_events ADD CONSTRAINT fk_calendar_events_user FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE CASCADE;


-- ending_schema.chat_messages foreign keys

ALTER TABLE ending_schema.chat_messages ADD CONSTRAINT fk_chat_messages_user FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE CASCADE;


-- ending_schema.deceased_profiles foreign keys

ALTER TABLE ending_schema.deceased_profiles ADD CONSTRAINT fk_deceased_profiles_user FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE CASCADE;


-- ending_schema.notifications foreign keys

ALTER TABLE ending_schema.notifications ADD CONSTRAINT fk_notifications_deceased_profile FOREIGN KEY (deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.notifications ADD CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.notifications ADD CONSTRAINT fk_notifications_user_procedure_checklist FOREIGN KEY (user_procedure_checklist_id) REFERENCES ending_schema.user_procedure_checklists(user_procedure_checklist_id) ON DELETE CASCADE;


-- ending_schema.refresh_tokens foreign keys

ALTER TABLE ending_schema.refresh_tokens ADD CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE CASCADE;


-- ending_schema.survey_responses foreign keys

ALTER TABLE ending_schema.survey_responses ADD CONSTRAINT fk_survey_responses_answer FOREIGN KEY (survey_answer_id) REFERENCES ending_schema.survey_answers(survey_answer_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.survey_responses ADD CONSTRAINT fk_survey_responses_deceased_profile FOREIGN KEY (deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id) ON DELETE CASCADE;


-- ending_schema.user_document_checklists foreign keys

ALTER TABLE ending_schema.user_document_checklists ADD CONSTRAINT fk_user_document_checklists_deceased_profile FOREIGN KEY (deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.user_document_checklists ADD CONSTRAINT fk_user_document_checklists_procedure_document FOREIGN KEY (procedure_document_id) REFERENCES ending_schema.procedure_documents(procedure_document_id);


-- ending_schema.user_event_logs foreign keys

ALTER TABLE ending_schema.user_event_logs ADD CONSTRAINT user_event_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES ending_schema.users(user_id) ON DELETE SET NULL;


-- ending_schema.user_procedure_checklists foreign keys

ALTER TABLE ending_schema.user_procedure_checklists ADD CONSTRAINT fk_user_procedure_checklists_deceased_profile FOREIGN KEY (deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id) ON DELETE CASCADE;
ALTER TABLE ending_schema.user_procedure_checklists ADD CONSTRAINT fk_user_procedure_checklists_procedure FOREIGN KEY (procedure_id) REFERENCES ending_schema."procedures"(procedure_id);


-- ending_schema.users foreign keys

ALTER TABLE ending_schema.users ADD CONSTRAINT fk_users_active_deceased_profile FOREIGN KEY (active_deceased_profile_id) REFERENCES ending_schema.deceased_profiles(deceased_profile_id);