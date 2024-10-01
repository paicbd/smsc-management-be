create table balance_type
(
    name varchar(255) not null
        primary key
);

create table bind_statuses
(
    state varchar(255) not null
        primary key
);

create table binds_types
(
    _type varchar(255) not null
        primary key
);

create table common_variables
(
    data_type varchar(255),
    key       varchar(255) not null
        primary key,
    value     varchar(255)
);

create table delivery_status
(
    name  varchar(255),
    value varchar(255) not null
        primary key
);

create table diameter
(
    enabled    integer,
    id         integer not null
        primary key,
    connection varchar(255),
    name       varchar(255)
);

create table diameter_applications
(
    acct_appl_id integer,
    auth_appl_id integer,
    id           integer not null
        primary key,
    vendor_id    integer,
    name         varchar(255),
    type         varchar(255)
);

create table diameter_local_peer
(
    diameter_id       integer
        constraint fk9pmh2ggbo111y6hkcqvtrbfje
            references diameter,
    firmware_revision integer,
    id                integer not null
        primary key,
    vendor_id         integer,
    ip_address        varchar(255),
    product_name      varchar(255),
    realm             varchar(255),
    uri               varchar(255)
);

create table diameter_parameters
(
    accept_undefined_peer boolean,
    cea_time_out          integer,
    diameter_id           integer
        constraint fk56n9hdf1q3obaieh5kx4sfudh
            references diameter,
    dpa_time_out          integer,
    duplicate_protection  boolean,
    duplicate_size        integer,
    duplicate_timer       integer,
    dwa_time_out          integer,
    iac_time_out          integer,
    id                    integer not null
        primary key,
    message_time_out      integer,
    peer_fsm_thread_count integer,
    queue_size            integer,
    rec_time_out          integer,
    stop_time_out         integer
);

create table diameter_peers
(
    attempt_connect boolean,
    diameter_id     integer
        constraint fkt3tv9wa6b717akut7emc59cs
            references diameter,
    id              integer not null
        primary key,
    port            integer,
    rating          integer,
    use_uri_as_fqdn boolean,
    host            varchar(255),
    ip              varchar(255),
    name            varchar(255),
    type            varchar(255),
    uri             varchar(255)
);

create table diameter_realms
(
    application_id integer
        constraint fkhnmhykibn75b1ba5dii7tn961
            references diameter_applications,
    dynamic        boolean,
    exp_time       integer,
    id             integer not null
        primary key,
    peer_id        integer
        constraint fktjwdgvf3rjsmu8gk8mhok5d0w
            references diameter_peers,
    domain         varchar(255),
    local_action   varchar(255),
    name           varchar(255)
);

create table encoding_type
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table functionality
(
    id   varchar(255) not null
        primary key,
    name varchar(255)
);

create table general_settings_smpp_http
(
    dest_addr_npi       integer,
    dest_addr_ton       integer,
    encoding_gsm7       integer
        constraint fk62bsxkem9cnnfcyjc20ttvalp
            references encoding_type,
    encoding_iso88591   integer
        constraint fk9phdtwk9196ywhlxwq6n12ak7
            references encoding_type,
    encoding_ucs2       integer
        constraint fkdob2n4cx5pnlt6bk0xsqsqhwr
            references encoding_type,
    id                  integer not null
        primary key,
    max_validity_period integer,
    source_addr_npi     integer,
    source_addr_ton     integer,
    validity_period     integer
);

create table general_smsc_retry
(
    first_retry_delay      integer,
    id                     integer not null
        primary key,
    max_due_delay          integer,
    retry_delay_multiplier integer
);

create table global_title_indicator
(
    gt_indicator    varchar(255),
    gt_indicator_id varchar(255) not null
        primary key
);

create table interfaz_versions
(
    id      varchar(255) not null
        primary key,
    version varchar(255)
);

create table load_sharing_algorithm
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table nature_of_address
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table npi_catalog
(
    id          integer not null
        primary key,
    description varchar(255)
);

create table numbering_plan
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table origination_type
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table rule_type
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table sequence_networks_id
(
    id           integer not null
        primary key,
    network_type varchar(255)
);

create table sls_range
(
    id   varchar(255) not null
        primary key,
    name varchar(255)
);

create table ton_catalog
(
    id          integer not null
        primary key,
    description varchar(255)
);

create table routing_rules
(
    check_sri_response              boolean default false,
    drop_map_sri                    boolean default false,
    drop_temp_failure               boolean,
    id                              integer not null
        primary key,
    is_sri_response                 boolean default false,
    network_id_temp_failure         integer
        constraint fkcky69a4h9d4yq0920sqfrxe8t
            references sequence_networks_id,
    network_id_to_map_sri           integer
        constraint fkbxsovx8b75kldkylerv0sm0wn
            references sequence_networks_id,
    network_id_to_permanent_failure integer
        constraint fka35h5i1rs9v2bperkxh2vw7vs
            references sequence_networks_id,
    new_dest_addr_npi               integer
        constraint fk6soe35of9gh35sco3abb7k661
            references npi_catalog,
    new_dest_addr_ton               integer
        constraint fk8ywuwyn0e95p6budw28md2v9q
            references ton_catalog,
    new_source_addr_npi             integer
        constraint fksj71vtk8uevl1htib8xhmi3pc
            references npi_catalog,
    new_source_addr_ton             integer
        constraint fk9w3j7oj5fm97yeqm0euf3asjy
            references ton_catalog,
    origin_network_id               integer
        constraint fkhg3nwsyatxey2tamhednigjh5
            references sequence_networks_id,
    add_dest_addr_prefix            varchar(255),
    add_source_addr_prefix          varchar(255),
    new_destination_addr            varchar(255),
    new_gt_sccp_addr_mt             varchar(255),
    new_source_addr                 varchar(255),
    regex_calling_party_address     varchar(255),
    regex_dest_addr_npi             varchar(255),
    regex_dest_addr_ton             varchar(255),
    regex_destination_addr          varchar(255),
    regex_imsi_digits_mask          varchar(255),
    regex_network_node_number       varchar(255),
    regex_source_addr               varchar(255),
    regex_source_addr_npi           varchar(255),
    regex_source_addr_ton           varchar(255),
    remove_dest_addr_prefix         varchar(255),
    remove_source_addr_prefix       varchar(255)
);

create table routing_rules_destination
(
    id               integer not null
        primary key,
    network_id       integer
        constraint fk3ensnwtyfhohht16gardgcrn3
            references sequence_networks_id,
    priority         integer not null,
    routing_rules_id integer
        constraint fkcus6n2dflfexfmmstumie14m0
            references routing_rules,
    network_type     varchar(255)
);

create table traffic_mode
(
    id   integer not null
        primary key,
    name varchar(255)
);

create table m3ua_application_server
(
    id                        integer not null
        primary key,
    minimum_asp_for_loadshare integer,
    network_appearance        integer,
    routing_context           integer,
    traffic_mode_id           integer
        constraint fkca0mtgjxb1w3dni3mci1yaoyo
            references traffic_mode,
    exchange                  varchar(255),
    functionality             varchar(255)
        constraint fkfj181ikvmmcb1hdw3lrapxdwe
            references functionality,
    name                      varchar(255),
    state                     varchar(255)
);

create table m3ua_routes
(
    destination_point_code integer,
    id                     integer not null
        primary key,
    m3ua_id                integer,
    origination_point_code integer,
    service_indicator      integer,
    traffic_mode_id        integer
        constraint fkh5acdxuunq5xgabs9g4j1aea9
            references traffic_mode,
    constraint m3ua_routes_m3ua_id_origination_point_code_destination_poin_key
        unique (m3ua_id, origination_point_code, destination_point_code, service_indicator),
    constraint ukgyalwjug4ny0f9ctleav2krsq
        unique (m3ua_id, origination_point_code, destination_point_code, service_indicator)
);

create table m3ua_app_servers_routes
(
    application_server_id integer
        constraint fkop7ko7rwoucyf9oybppggii4v
            references m3ua_application_server,
    id                    integer not null
        primary key,
    route_id              integer
        constraint fkq1g2prfrlsteb2npwurmsmwxc
            references m3ua_routes,
    unique (route_id, application_server_id),
    constraint ukebqqhwax6g5205v6k3nh4ogv5
        unique (route_id, application_server_id)
);

create table users
(
    id        integer not null
        primary key,
    status    smallint,
    last_name varchar(255),
    name      varchar(255),
    password  varchar(255),
    role      varchar(255),
    user_name varchar(255)
);

create table delivery_error_code
(
    created_by_id integer
        constraint fko62geb4dkg6yajp1ixm3q72h9
            references users,
    id            integer      not null
        primary key,
    updated_by_id integer
        constraint fk82mxdl1clh7wynqn52qyhp260
            references users,
    created_at    timestamp(6),
    updated_at    timestamp(6),
    code          varchar(255)
        unique,
    description   varchar(255) not null
);

create table operator_mno
(
    created_by_id             integer
        constraint fkh31cq7s2c2l3jj6n94i44x7wd
            references users,
    enabled                   boolean default true,
    id                        integer not null
        primary key,
    message_id_decimal_format boolean,
    tlv_message_receipt_id    boolean,
    updated_by_id             integer
        constraint fkkbghhekhpgu3bl0noand2wolq
            references users,
    created_at                timestamp(6),
    updated_at                timestamp(6),
    name                      varchar(255)
        unique
);

create table error_code
(
    created_by_id integer
        constraint fkehkcnny5wavo8tvh5sl9d9iom
            references users,
    id            integer      not null
        primary key,
    mno_id        integer
        constraint fklreaj5j5e3gic3078qg05ikuy
            references operator_mno,
    updated_by_id integer
        constraint fkqmsok2xjh5esavnr6c7ojphdb
            references users,
    created_at    timestamp(6),
    updated_at    timestamp(6),
    code          varchar(255),
    description   varchar(255) not null,
    unique (code, mno_id),
    constraint uk15bb2qv9ktfalxfpitj5x58r7
        unique (code, mno_id)
);

create table error_code_mapping
(
    created_by_id          integer
        constraint fk918xa3bh75vo327bv4g6j078l
            references users,
    delivery_error_code_id integer
        constraint fk945oyjc2n6yp12kvgcbx17sxc
            references delivery_error_code,
    error_code_id          integer
        constraint fk2we8j4idp8xbdjjajaxg0muiu
            references error_code,
    id                     integer not null
        primary key,
    updated_by_id          integer
        constraint fkk1df61wbr9lj066kh97x3txbl
            references users,
    created_at             timestamp(6),
    updated_at             timestamp(6),
    delivery_status_id     varchar(255)
        constraint fkojoedwssx6jilchx3tkhahqsx
            references delivery_status,
    unique (error_code_id, delivery_error_code_id),
    constraint uk4v8vwdmj5kibt2iw19asg2lhy
        unique (error_code_id, delivery_error_code_id)
);

create table gateways
(
    active_sessions_numbers                integer,
    address_npi                            integer default 0                     not null
        constraint fkafcy4fm53nealuu6ngo5e1sdv
            references npi_catalog,
    address_ton                            integer default 0                     not null
        constraint fk8e9ba9lhkkiagdb1qvj59t6mf
            references ton_catalog,
    bind_retry_period                      integer default 10000,
    bind_timeout                           integer default 5000,
    created_by_id                          integer
        constraint fk70y9wv9t3sysh1ev8onthxxrs
            references users,
    enabled                                integer default 0,
    encoding_gsm7                          integer
        constraint fk5d46cpsyvq0vk2renvj4ultoy
            references encoding_type,
    encoding_iso88591                      integer
        constraint fkthmdt811l9lleiqshh7exj35b
            references encoding_type,
    encoding_ucs2                          integer
        constraint fkc57rphbixo3h1ade0xj8ptbb6
            references encoding_type,
    enquire_link_period                    integer default 30000,
    mno_id                                 integer                               not null
        constraint fkp3fy2c2ba20faukx1haf2rab5
            references operator_mno,
    network_id                             integer                               not null
        primary key
        constraint fkmdyrp5w7ky69i9h48k7imay6i
            references sequence_networks_id,
    pdu_degree                             integer default 1                     not null,
    pdu_timeout                            integer default 5000,
    port                                   integer                               not null,
    request_dlr                            boolean default false,
    sessions_number                        integer default 1                     not null,
    split_message                          boolean default false,
    thread_pool_size                       integer default 100                   not null,
    tps                                    integer default 1,
    updated_by_id                          integer
        constraint fkqbhoblk72p8hhccwmhvcoajw1
            references users,
    created_at                             timestamp(6),
    updated_at                             timestamp(6),
    address_range                          text    default '^[0-9a-zA-Z]*'::text not null,
    auto_retry_error_code                  text    default ''::text,
    bind_type                              text                                  not null
        constraint fk1hr3w93a1nq7srmhqebwo3bp3
            references binds_types,
    interface_version                      text    default 'IF_34'::text         not null
        constraint fkh2lo1hgy0juulwpamegp0e41v
            references interfaz_versions,
    ip                                     text                                  not null,
    name                                   text                                  not null,
    no_retry_error_code                    varchar(255),
    password                               text                                  not null,
    protocol                               varchar(255),
    retry_alternate_destination_error_code varchar(255),
    split_smpp_type                        text    default 'TLV'::text,
    status                                 text    default 'CLOSED'::text
        constraint fkst7s9ydlycyps8oedxkf08po0
            references bind_statuses,
    system_id                              text                                  not null,
    system_type                            text                                  not null
);

create table service_provider
(
    active_sessions_numbers integer,
    address_npi             integer default 0                     not null
        constraint fk72e5dtxmgl4oasa4q05wrrfds
            references npi_catalog,
    address_ton             integer default 0                     not null
        constraint fk4gmn1wynsdo7tw9wmiqf3k2g0
            references ton_catalog,
    created_by_id           integer
        constraint fksc22owlnmdic39itv8yqpy7xf
            references users,
    enabled                 integer default 0,
    enquire_link_period     integer default 30000,
    network_id              integer                               not null
        primary key
        constraint fko5i3ogd2379v2rs0uv67y2wgj
            references sequence_networks_id,
    pdu_timeout             integer default 5000,
    request_dlr             boolean default false,
    sessions_number         integer default 1                     not null,
    tps                     integer default 1,
    updated_by_id           integer
        constraint fk6hy1e9qtfsa0gr2994mnu09si
            references users,
    validity                integer default 0,
    balance                 bigint  default 0,
    created_at              timestamp(6),
    updated_at              timestamp(6),
    address_range           text    default '^[0-9a-zA-Z]*'::text not null,
    authentication_types    varchar(255),
    balance_type            text    default 'PREPAID'::text
        constraint fkbb8f91tf8dsnxt6g4uaqupms8
            references balance_type,
    callback_url            varchar(255),
    contact_name            varchar(255),
    email                   varchar(255),
    header_security_name    varchar(255),
    interface_version       text    default 'IF_34'::text         not null
        constraint fk6ma0f6uk7bv59nxixtgnctuok
            references interfaz_versions,
    name                    text                                  not null,
    passwd                  varchar(255),
    password                text                                  not null,
    phone_number            varchar(255),
    protocol                varchar(255),
    status                  text    default 'CLOSED'::text
        constraint fk44prj5j85dguvrnolnsref45x
            references bind_statuses,
    system_id               text                                  not null,
    system_type             text                                  not null,
    token                   varchar(255),
    user_name               varchar(255),
    bind_type               text         default 'TRANSCEIVER'::text   not null
);

create table callback_header_http
(
    id           integer not null
        primary key,
    network_id   integer
        constraint fk94broioxmfjf2x4q81in88fb4
            references service_provider,
    header_name  varchar(255),
    header_value varchar(255)
);

create table credit_sales_history
(
    created_by_id integer
        constraint fkc78whab4gorymw2ggs78fhvyd
            references users,
    id            integer not null
        primary key,
    network_id    integer
        constraint fkg5m57ufr6t67rmh4c5pgdjipl
            references service_provider,
    updated_by_id integer
        constraint fk5qduc30gwf8o0c84hctlccpgv
            references users,
    created_at    timestamp(6),
    credit        bigint default 0,
    updated_at    timestamp(6),
    description   varchar(255)
);

create table ss7_gateways
(
    created_by_id          integer
        constraint fke398mjq1j5s3sdtrtnebwcdhl
            references users,
    enabled                integer default 1,
    hlr_ssn                integer default 6,
    map_version            integer default 3,
    mno_id                 integer not null
        constraint fkpe3wpm3d1gva1hcmduaxmjexs
            references operator_mno,
    msc_ssn                integer default 8,
    network_id             integer not null
        primary key
        constraint fk6ee1kvbwoy8w0uaplhepa6wbx
            references sequence_networks_id,
    smsc_ssn               integer default 8,
    split_message          boolean default false,
    translation_type       integer default 0,
    updated_by_id          integer
        constraint fkmpuvx32oewf6d9qh5cd9dv5f4
            references users,
    created_at             timestamp(6),
    updated_at             timestamp(6),
    global_title           text    default ''::text,
    global_title_indicator text    default '0100'::text
        constraint fkamb81g63dtvvv43vahasht64m
            references global_title_indicator,
    name                   text    not null
        unique,
    protocol               text    default 'SS7'::text,
    status                 text    default 'STARTED'::text
);

create table m3ua
(
    cc_delay_back_to_normal_threshold_1 numeric,
    cc_delay_back_to_normal_threshold_2 numeric,
    cc_delay_back_to_normal_threshold_3 numeric,
    cc_delay_threshold_1                numeric,
    cc_delay_threshold_2                numeric,
    cc_delay_threshold_3                numeric,
    connect_delay                       integer,
    heart_beat_time                     integer,
    id                                  integer not null
        primary key,
    max_for_route                       integer,
    max_sequence_number                 integer,
    network_id                          integer
        unique
        constraint fkoxu3uwdhl9ufmrx8xh0wj7kco
            references ss7_gateways,
    routing_key_management_enabled      boolean,
    thread_count                        integer,
    use_lowest_bit_for_link             boolean,
    routing_label_format                varchar(255)
);

create table m3ua_sockets
(
    enabled                    integer default 0,
    host_port                  integer,
    id                         integer not null
        primary key,
    max_concurrent_connections integer,
    ss7_m3ua_id                integer
        constraint fkc3lkb2mbueteg4af7y4j5cb99
            references m3ua,
    extra_address              varchar(255),
    host_address               varchar(255),
    name                       varchar(255),
    socket_type                varchar(255),
    state                      varchar(255),
    transport_type             varchar(255)
);

create table m3ua_associations
(
    enabled        integer default 0,
    id             integer not null
        primary key,
    m3ua_heartbeat boolean,
    m3ua_socket_id integer
        constraint fkn9f0dqurj9pjimyfbkijyhkd1
            references m3ua_sockets,
    peer_port      integer,
    asp_name       varchar(255),
    name           varchar(255),
    peer           varchar(255),
    state          varchar(255)
);

create table m3ua_ass_app_servers
(
    application_server_id integer
        constraint fkmr9a2epwq9m0bv3u2cpde8b0j
            references m3ua_application_server,
    asp_id                integer
        constraint fkboq2hgut7uso5aqrvocu0199s
            references m3ua_associations,
    id                    integer not null
        primary key,
    unique (asp_id, application_server_id),
    constraint ukmd89q1mobwwfpckj8rcpm3h7p
        unique (asp_id, application_server_id)
);

create table map
(
    forward_sm_service_op_code integer,
    id                         integer not null
        primary key,
    network_id                 integer
        unique
        constraint fkqj3226nferd1nk5rng5qki9oa
            references ss7_gateways,
    sri_service_op_code        integer
);

create table sccp
(
    congestion_control                 boolean,
    congestion_control_timer_a         integer,
    congestion_control_timer_d         integer,
    id                                 integer not null
        primary key,
    max_data_message                   integer,
    network_id                         integer
        unique
        constraint fki502jqvnmt4aa9lk7wdb4e80t
            references ss7_gateways,
    period_of_logging                  integer,
    preview_mode                       boolean,
    reassembly_timer_delay             integer,
    remove_spc                         boolean,
    sst_timer_duration_increase_factor numeric,
    sst_timer_duration_max             integer,
    sst_timer_duration_min             integer,
    z_margin_xudt_message              integer,
    congestion_control_algorithm       varchar(255),
    sccp_protocol_version              varchar(255)
);

create table sccp_addresses
(
    address_indicator    integer,
    id                   integer not null
        primary key,
    nature_of_address_id integer
        constraint fkajr598l3ib742nyfa8wg5kx06
            references nature_of_address,
    numbering_plan_id    integer
        constraint fkrwhheucorf0e1myip2kusi0ex
            references numbering_plan,
    point_code           integer,
    ss7_sccp_id          integer
        constraint fkaphrbjdtkfeb9sd0w9sp88tvc
            references sccp,
    subsystem_number     integer,
    translation_type     integer,
    digits               varchar(255),
    gt_indicator         varchar(255),
    name                 varchar(255)
);

create table sccp_remote_resources
(
    id                 integer not null
        primary key,
    mark_prohibited    boolean,
    remote_spc         integer,
    remote_ssn         integer,
    ss7_sccp_id        integer
        constraint fkdlfbwrrb24se7hdsv94huwe7i
            references sccp,
    remote_sccp_status varchar(255),
    remote_spc_status  varchar(255),
    remote_ssn_status  varchar(255)
);

create table sccp_rules
(
    address_indicator            integer,
    calling_address_indicator    integer,
    calling_nature_of_address_id integer
        constraint fkqmf0x1xndysrdgyg86qrgatjt
            references nature_of_address,
    calling_numbering_plan_id    integer
        constraint fkr66d786woca1eaa25ecy43onk
            references numbering_plan,
    calling_point_code           integer,
    calling_subsystem_number     integer,
    calling_translator_type      integer,
    id                           integer not null
        primary key,
    load_sharing_algorithm_id    integer
        constraint fk971cp4mn25v74d68u8jueaqn
            references load_sharing_algorithm,
    nature_of_address_id         integer
        constraint fknpjfcqe0d91lwao1v8ryu5ui2
            references nature_of_address,
    numbering_plan_id            integer
        constraint fkgp4dyceoegce1joqroq4hxf86
            references numbering_plan,
    origination_type_id          integer
        constraint fk9aq6pvmroxx2jp4wrfljtfge8
            references origination_type,
    point_code                   integer,
    primary_address_id           integer
        constraint fkjpeyrux99m5i6p8u2q4gms79
            references sccp_addresses,
    rule_type_id                 integer
        constraint fk86un5bs185j4py6ld0hmm36a3
            references rule_type,
    secondary_address_id         integer
        constraint fkcv4gi3o8dprt103fpnub0bytw
            references sccp_addresses,
    subsystem_number             integer,
    translation_type             integer,
    calling_global_tittle_digits varchar(255),
    calling_gt_indicator         varchar(255),
    global_tittle_digits         varchar(255),
    gt_indicator                 varchar(255),
    mask                         varchar(255),
    name                         varchar(255),
    new_calling_party_address    varchar(255)
);

create table sccp_service_access_points
(
    id                integer not null
        primary key,
    network_indicator integer,
    origin_point_code integer,
    ss7_sccp_id       integer
        constraint fk6cjknd0shucvcemy7dqc8o0b1
            references sccp,
    local_gt_digits   varchar(255),
    name              varchar(255)
);

create table sccp_mtp3_destinations
(
    first_point_code integer,
    first_sls        integer,
    id               integer not null
        primary key,
    last_point_code  integer,
    last_sls         integer,
    sccp_sap_id      integer
        constraint fkstbbmxonimogy3avxjga1gxos
            references sccp_service_access_points,
    sls_mask         integer,
    name             varchar(255)
);

create table tcap
(
    blocking_incoming_tcap_messages boolean,
    dialog_id_range_end             integer,
    dialog_id_range_start           integer,
    dialog_idle_timeout             integer,
    do_not_send_protocol_version    boolean,
    exr_back_to_normal_delay_thr1   numeric,
    exr_back_to_normal_delay_thr2   numeric,
    exr_back_to_normal_delay_thr3   numeric,
    exr_delay_thr1                  numeric,
    exr_delay_thr2                  numeric,
    exr_delay_thr3                  numeric,
    id                              integer not null
        primary key,
    invoke_timeout                  integer,
    max_dialogs                     integer,
    mem_back_to_normal_delay_thr1   numeric,
    mem_back_to_normal_delay_thr2   numeric,
    mem_back_to_normal_delay_thr3   numeric,
    memory_monitor_thr1             numeric,
    memory_monitor_thr2             numeric,
    memory_monitor_thr3             numeric,
    network_id                      integer
        unique
        constraint fkbqi8vmhhfy2jvu9d6bc0fye12
            references ss7_gateways,
    preview_mode                    boolean,
    ssn_list                        integer,
    swap_tcap_id_enabled            boolean,
    sls_range_id                    varchar(255)
        constraint fkm0giwue0dwid958e7qlpi2qt8
            references sls_range
);

insert into public.bind_statuses (state)
values  ('STOPPED'),
        ('STARTED'),
        ('BINDING'),
        ('BOUND'),
        ('UNBINDING'),
        ('UNBOUND');

insert into public.balance_type (name)
values  ('PREPAID'),
        ('POSTPAID');

insert into public.binds_types (_type)
values  ('TRANSMITTER'),
        ('RECEIVER'),
        ('TRANSCEIVER');

insert into public.delivery_status (name, value)
values  ('ENROUTE', 'ENROUTE'),
        ('DELIVERED', 'DELIVRD'),
        ('EXPIRED', 'EXPIRED'),
        ('DELETED', 'DELETED'),
        ('UNDELIVERED', 'UNDELIV'),
        ('ACCEPTED', 'ACCEPTD'),
        ('UNKNOWN', 'UNKNOWN'),
        ('REJECTED', 'REJECTD');

insert into public.encoding_type (id, name)
values  (0, 'GSM7'),
        (1, 'UTF8'),
        (2, 'UNICODE'),
        (3, 'ISO88591');

insert into public.interfaz_versions (id, version)
values  ('IF_33', '3.3'),
        ('IF_34', '3.4'),
        ('IF_50', '5.0');

insert into public.global_title_indicator (gt_indicator, gt_indicator_id)
values  ('GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY', 'GT0001'),
        ('GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY', 'GT0010'),
        ('GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME', 'GT0011'),
        ('GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS', 'GT0100');

insert into public.load_sharing_algorithm (id, name)
values  (1, 'UNDEFINED'),
        (2, 'Bit0'),
        (3, 'Bit1'),
        (4, 'Bit2'),
        (5, 'Bit3'),
        (6, 'Bit4');

insert into public.nature_of_address (id, name)
values  (-1, 'UNDEFINED'),
        (0, ' UNKNOWN'),
        (1, ' SUBSCRIBER'),
        (2, ' RESERVED_NATIONAL_2'),
        (3, ' NATIONAL'),
        (4, ' INTERNATIONAL'),
        (5, ' SPARE_5'),
        (6, ' SPARE_6'),
        (7, ' SPARE_7'),
        (8, ' SPARE_8'),
        (9, ' SPARE_9'),
        (10, 'SPARE_10'),
        (11, ' SPARE_11'),
        (12, ' SPARE_12'),
        (13, ' SPARE_13'),
        (14, ' SPARE_14'),
        (15, ' SPARE_15'),
        (16, ' SPARE_16'),
        (17, ' SPARE_17'),
        (18, ' SPARE_18'),
        (19, ' SPARE_19'),
        (20, ' SPARE_20'),
        (21, ' SPARE_21'),
        (22, ' SPARE_22'),
        (23, 'SPARE_23'),
        (24, ' SPARE_24'),
        (25, ' SPARE_25'),
        (26, ' SPARE_26'),
        (27, ' SPARE_27'),
        (28, ' SPARE_28'),
        (29, ' SPARE_29'),
        (30, ' SPARE_30'),
        (31, ' SPARE_31'),
        (32, ' SPARE_32'),
        (33, ' SPARE_33'),
        (34, ' SPARE_34'),
        (35, ' SPARE_35'),
        (36, 'SPARE_36'),
        (37, ' SPARE_37'),
        (38, ' SPARE_38'),
        (39, ' SPARE_39'),
        (40, ' SPARE_40'),
        (41, ' SPARE_41'),
        (42, ' SPARE_42'),
        (43, ' SPARE_43'),
        (44, ' SPARE_44'),
        (45, ' SPARE_45'),
        (46, ' SPARE_46'),
        (47, ' SPARE_47'),
        (48, ' SPARE_48'),
        (49, ' SPARE_49'),
        (50, ' SPARE_50'),
        (51, ' SPARE_51'),
        (52, ' SPARE_52'),
        (53, ' SPARE_53'),
        (54, ' SPARE_54'),
        (55, ' SPARE_55'),
        (56, ' SPARE_56'),
        (57, ' SPARE_57'),
        (58, ' SPARE_58'),
        (59, ' SPARE_59'),
        (60, ' SPARE_60'),
        (61, ' SPARE_61'),
        (62, ' SPARE_62'),
        (63, ' SPARE_63'),
        (64, ' SPARE_64'),
        (65, ' SPARE_65'),
        (66, ' SPARE_66'),
        (67, ' SPARE_67'),
        (68, ' SPARE_68'),
        (69, ' SPARE_69'),
        (70, ' SPARE_70'),
        (71, ' SPARE_71'),
        (72, ' SPARE_72'),
        (73, ' SPARE_73'),
        (74, ' SPARE_74'),
        (75, ' SPARE_75'),
        (76, ' SPARE_76'),
        (77, ' SPARE_77'),
        (78, ' SPARE_78'),
        (79, ' SPARE_79'),
        (80, ' SPARE_80'),
        (81, ' SPARE_81'),
        (82, ' SPARE_82'),
        (83, ' SPARE_83'),
        (84, ' SPARE_84'),
        (85, ' SPARE_85'),
        (86, ' SPARE_86'),
        (87, ' SPARE_87'),
        (88, ' SPARE_88'),
        (89, ' SPARE_89'),
        (90, ' SPARE_90'),
        (91, ' SPARE_91'),
        (92, ' SPARE_92'),
        (93, ' SPARE_93'),
        (94, ' SPARE_94'),
        (95, ' SPARE_95'),
        (96, ' SPARE_96'),
        (97, ' SPARE_97'),
        (98, ' SPARE_98'),
        (99, ' SPARE_99'),
        (100, ' SPARE_100'),
        (101, ' SPARE_101'),
        (102, ' SPARE_102'),
        (103, ' SPARE_103'),
        (104, ' SPARE_104'),
        (105, ' SPARE_105'),
        (106, ' SPARE_106'),
        (107, ' SPARE_107'),
        (108, ' SPARE_108'),
        (109, ' SPARE_109'),
        (110, ' SPARE_110'),
        (111, ' SPARE_111'),
        (112, ' RESERVED_NATIONAL_112'),
        (113, ' RESERVED_NATIONAL_113'),
        (114, ' RESERVED_NATIONAL_114'),
        (115, ' RESERVED_NATIONAL_115'),
        (116, ' RESERVED_NATIONAL_116'),
        (117, ' RESERVED_NATIONAL_117'),
        (118, ' RESERVED_NATIONAL_118'),
        (119, ' RESERVED_NATIONAL_119'),
        (120, ' RESERVED_NATIONAL_120'),
        (121, ' RESERVED_NATIONAL_121'),
        (122, ' RESERVED_NATIONAL_122'),
        (123, ' RESERVED_NATIONAL_123'),
        (124, ' RESERVED_NATIONAL_124'),
        (125, ' RESERVED_NATIONAL_125'),
        (126, ' RESERVED_NATIONAL_126'),
        (127, ' RESERVED');

insert into public.npi_catalog (id, description)
values  (-1, 'Default'),
        (0, 'Unknown'),
        (1, 'ISDN'),
        (3, 'Data'),
        (4, 'Telex'),
        (6, 'Land Mobile'),
        (8, 'National'),
        (9, 'Private'),
        (10, 'ERMES'),
        (14, 'Internet (IP)'),
        (18, 'WAP');

insert into public.numbering_plan (id, name)
values  (-1, 'UNDEFINED'),
        (0, 'unknown'),
        (1, 'ISDN'),
        (2, 'spare_2'),
        (3, 'data'),
        (4, 'telex'),
        (5, 'spare_5'),
        (6, 'land_mobile'),
        (7, 'spare_7'),
        (8, 'national'),
        (9, 'private_plan'),
        (15, 'reserved');

insert into public.origination_type (id, name)
values  (1, 'All'),
        (2, 'LocalOriginated'),
        (3, 'RemoteOriginated');

insert into public.rule_type (id, name)
values  (1, 'Solitary'),
        (2, 'Dominant'),
        (3, 'Loadshared'),
        (4, 'Broadcast');

insert into public.sls_range (id, name)
values  ('All', 'All'),
        ('Odd', 'Odd'),
        ('Even', 'Even');

insert into public.ton_catalog (id, description)
values  (-1, 'Default'),
        (0, 'Unknown'),
        (1, 'International'),
        (2, 'National'),
        (3, 'Network Specific'),
        (4, 'Subscriber Number'),
        (5, 'Alphanumeric'),
        (6, 'Abbreviated');

insert into public.functionality (id, name)
values  ('SGW', 'SGW'),
        ('AS', 'AS'),
        ('IPSP-CLIENT', 'IPSP Client'),
        ('IPSP-SERVER', 'IPSP Server');

insert into public.traffic_mode (id, name)
values  (1, 'Override'),
        (2, 'Loadshare'),
        (3, 'Broadcast');

insert into public.diameter_applications (id, acct_appl_id, auth_appl_id, name, type, vendor_id)
values  (1, 0, 4, 'Diameter Credit Control', 'OCS', 0),
        (2, 0, 16777238, '16777238 3GPP Gx 29.212', 'PCRF', 10415),
        (3, 0, 16777224, '16777224 3GPP Gx 29.210', 'PCRF', 10415),
        (4, 0, 16777225, '16777225 3GPP Gx over Gy 29.210', 'PCRF', 10415);

insert into public.users (id, status, last_name, name, password, role, user_name)
values  (1, 1, 'ROOT', 'ROOT', '$2a$10$42dNxrw.5Y46TU4tHNXwwO7g/Om9k0yVzepUdXmBD0e.gV/bNLrQW', 'ROOT', 'admin');


insert into public.common_variables(key, data_type, value)
values ('SMSC_ACCOUNT_SETTINGS', 'json', '{"max_password_length":16,"max_system_id_length":20}');

insert into public.common_variables(key, data_type, value)
values ('USE_LOCAL_CHARGING', 'boolean', 'false');

INSERT INTO public.general_settings_smpp_http (id, dest_addr_npi, dest_addr_ton, encoding_gsm7, encoding_iso88591, encoding_ucs2, max_validity_period, source_addr_npi, source_addr_ton, validity_period)
VALUES (1, 1, 1, 0, 3, 2, 240, 1, 1, 60);

INSERT INTO public.general_smsc_retry (id, first_retry_delay, max_due_delay, retry_delay_multiplier)
VALUES (1, 0, 86400, 2);