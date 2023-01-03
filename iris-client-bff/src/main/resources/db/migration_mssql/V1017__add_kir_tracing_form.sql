CREATE TABLE kir_tracing_form (
	id uuid NOT NULL,
    access_token varchar(256) NOT NULL,
    status varchar(50) NOT NULL,
    target_disease varchar(50) NOT NULL,
    person_mobile_phone varchar(100) NOT NULL,
    srpSalt varchar(256) NOT NULL,
    srpVerifier varchar(256) NOT NULL,
	created timestamp NOT NULL,
	last_modified timestamp NOT NULL,
	created_by uuid NULL,
	last_modified_by uuid NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (created_by) REFERENCES user_accounts(user_id),
	FOREIGN KEY (last_modified_by) REFERENCES user_accounts(user_id)
);
