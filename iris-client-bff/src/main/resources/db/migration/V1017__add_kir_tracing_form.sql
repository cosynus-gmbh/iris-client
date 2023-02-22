CREATE TABLE kir_tracing_form (
	id uuid NOT NULL,
	access_token varchar(256) NOT NULL,
    status varchar(50) NOT NULL,
    assessment text NULL,
    therapy_results text NULL,
    target_disease varchar(50) NOT NULL,
    person_mobile_phone varchar(100) NULL,
	srp_salt varchar(512) NULL,
    srp_verifier text NULL,
    srp_session text NULL,
	created timestamp NOT NULL,
	last_modified timestamp NOT NULL,
	created_by uuid NULL,
	last_modified_by uuid NULL,
    deleted_at timestamp NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (created_by) REFERENCES user_accounts(user_id),
	FOREIGN KEY (last_modified_by) REFERENCES user_accounts(user_id)
);

CREATE TABLE kir_tracing_message (
    id uuid NOT NULL,
    form_id uuid NOT NULL,
    text text NOT NULL,
    created timestamp NOT NULL,
    last_modified timestamp NOT NULL,
    created_by uuid NULL,
    last_modified_by uuid NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (created_by) REFERENCES user_accounts(user_id),
    FOREIGN KEY (last_modified_by) REFERENCES user_accounts(user_id),
    CONSTRAINT kir_tracing_message_fk FOREIGN KEY (form_id) REFERENCES kir_tracing_form(id)
);
