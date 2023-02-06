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
	PRIMARY KEY (id),
	FOREIGN KEY (created_by) REFERENCES user_accounts(user_id),
	FOREIGN KEY (last_modified_by) REFERENCES user_accounts(user_id)
);
