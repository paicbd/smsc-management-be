package com.smsc.management.init;

import static com.smsc.management.utils.Constants.GENERAL_SETTINGS_SMPP_HTTP_ENDPOINT;
import static com.smsc.management.utils.Constants.GENERAL_SMSC_RETRY_ENDPOINT;
import static com.smsc.management.utils.Constants.KEY_MAX_PASSWORD_LENGTH;
import static com.smsc.management.utils.Constants.KEY_MAX_SYSTEM_ID_LENGTH;
import static com.smsc.management.utils.Constants.LOCAL_CHARGING;

import java.util.ArrayList;
import java.util.Objects;

import com.smsc.management.app.ss7.model.entity.SlsRange;
import com.smsc.management.app.ss7.model.repository.SlsRangeRepository;

import com.smsc.management.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smsc.management.app.settings.dto.GeneralSettingsSmppHttpDTO;
import com.smsc.management.app.settings.dto.GeneralSmscRetryDTO;
import com.smsc.management.app.catalog.model.entity.BalanceType;
import com.smsc.management.app.catalog.model.entity.BindStatuses;
import com.smsc.management.app.catalog.model.entity.BindsTypes;
import com.smsc.management.app.settings.model.entity.CommonVariables;
import com.smsc.management.app.catalog.model.entity.DeliveryStatus;
import com.smsc.management.app.catalog.model.entity.EncodingType;
import com.smsc.management.app.settings.model.entity.GeneralSettingsSmppHttp;
import com.smsc.management.app.settings.model.entity.GeneralSmscRetry;
import com.smsc.management.app.catalog.model.entity.InterfazVersions;
import com.smsc.management.app.catalog.model.entity.NpiCatalog;
import com.smsc.management.app.catalog.model.entity.TonCatalog;
import com.smsc.management.app.diameter.model.entity.Applications;
import com.smsc.management.app.diameter.model.entity.Diameter;
import com.smsc.management.app.ss7.model.entity.Functionality;
import com.smsc.management.app.ss7.model.entity.GlobalTitleIndicator;
import com.smsc.management.app.ss7.model.entity.LoadSharingAlgorithm;
import com.smsc.management.app.ss7.model.entity.OriginationType;
import com.smsc.management.app.ss7.model.entity.RuleType;
import com.smsc.management.app.ss7.model.entity.TrafficMode;
import com.smsc.management.app.settings.mapper.GeneralSettingsMapper;
import com.smsc.management.app.ss7.model.entity.NatureOfAddress;
import com.smsc.management.app.ss7.model.entity.NumberingPlan;
import com.smsc.management.app.catalog.model.repository.BalanceTypeRepository;
import com.smsc.management.app.catalog.model.repository.BindStatusesRepository;
import com.smsc.management.app.catalog.model.repository.BindsTypesRepository;
import com.smsc.management.app.settings.model.repository.CommonVariablesRepository;
import com.smsc.management.app.catalog.model.repository.DeliveryStatusRepository;
import com.smsc.management.app.catalog.model.repository.EncodingTypeRepository;
import com.smsc.management.app.settings.model.repository.GeneralSettingsSmppHttpRepository;
import com.smsc.management.app.settings.model.repository.GeneralSmscRetryRepository;
import com.smsc.management.app.catalog.model.repository.InterfaceVersionsRepository;
import com.smsc.management.app.catalog.model.repository.NpiCatalogRepository;
import com.smsc.management.app.catalog.model.repository.TonCatalogRepository;
import com.smsc.management.app.diameter.model.repository.ApplicationsRepository;
import com.smsc.management.app.diameter.model.repository.DiameterRepository;
import com.smsc.management.app.ss7.model.repository.FunctionalityRepository;
import com.smsc.management.app.ss7.model.repository.GlobalTitleIndicatorRepository;
import com.smsc.management.app.ss7.model.repository.LoadSharingAlgorithmRepository;
import com.smsc.management.app.ss7.model.repository.OriginationTypeRepository;
import com.smsc.management.app.ss7.model.repository.RuleTypeRepository;
import com.smsc.management.app.ss7.model.repository.TrafficModeRepository;
import com.smsc.management.app.ss7.model.repository.NatureOfAddressRepository;
import com.smsc.management.app.ss7.model.repository.NumberingPlanRepository;
import com.smsc.management.app.diameter.service.ObjectDiameterService;
import com.smsc.management.utils.UtilsBase;
import static com.smsc.management.utils.Constants.ENABLED_STATUS;
import static com.smsc.management.utils.Constants.SMSC_ACCOUNT_SETTINGS;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataInitializer {
	private final UserService userService;
	private final InterfaceVersionsRepository interfaceVersionsRepository;
	private final BindStatusesRepository bindStatusesRepository;
	private final NpiCatalogRepository npiRepo;
	private final TonCatalogRepository tonRepo;
	private final BalanceTypeRepository balanceTypeRepo;
	private final DeliveryStatusRepository deliveryStatusRepo;
	private final BindsTypesRepository bindsTypesRepo;
	private final FunctionalityRepository functionalityRepo;
	private final SlsRangeRepository slsRangeRepo;
	private final TrafficModeRepository trafficModeRepo;
	private final RuleTypeRepository sccpRuleTypeRepo;
	private final OriginationTypeRepository originationTypeRepo;
	private final LoadSharingAlgorithmRepository loadSharingAlgorithmRepo;
	private final NumberingPlanRepository numberingPlanRepo;
	private final NatureOfAddressRepository natureOfAddressRepo;
	private final EncodingTypeRepository encodingTypeRepo;
	private final GeneralSettingsSmppHttpRepository generalSettingsHttpSmppRepo;
	private final GeneralSmscRetryRepository generalSmscRetryRepo;
	private final GlobalTitleIndicatorRepository gtIndicatorRepo;
	private final ApplicationsRepository appRepo;
	private final CommonVariablesRepository commonVariablesRepo;

	private final GeneralSettingsMapper generalSettingsMapper;
	private final UtilsBase utilsBase;
	
	// diameter configurations
	private final DiameterRepository diameterRepo;
	private final ObjectDiameterService processDiameter;
	
	@Value("${general.settings}")
	private String hashTableGeneralSettings = "general_settings";
	
	@Value("${general.settings.key.smppHttp}")
	private String hashKeyGeneralSettingsSmppHttp = "smpp_http";
	
	@Value("${general.settings.key.smscRetry}")
	private String hashKeyGeneralSettingsSmscRetry = "smsc_retry";

	public DataInitializer(UserService userService, InterfaceVersionsRepository interfaceVersionsRepository,
                           BindStatusesRepository bindStatusesRepository, NpiCatalogRepository npiRepo, TonCatalogRepository tonRepo,
                           BalanceTypeRepository balanceTypeRepo, DeliveryStatusRepository deliveryStatusRepo,
                           BindsTypesRepository bindsTypesRepo, FunctionalityRepository functionalityRepo,
                           SlsRangeRepository slsRangeRepo, TrafficModeRepository trafficModeRepo, RuleTypeRepository sccpRuleTypeRepo,
                           OriginationTypeRepository originationTypeRepo, LoadSharingAlgorithmRepository loadSharingAlgorithmRepo,
                           NumberingPlanRepository numberingPlanRepo, NatureOfAddressRepository natureOfAddressRepo,
                           EncodingTypeRepository encodingTypeRepo, GeneralSettingsSmppHttpRepository generalSettingsHttpSmppRepo,
                           GeneralSettingsMapper generalSettingsMapper, UtilsBase utilsBase, GeneralSmscRetryRepository generalSmscRetryRepo,
                           GlobalTitleIndicatorRepository gtIndicatorRepo, ApplicationsRepository appRepo, ObjectDiameterService processDiameter,
                           DiameterRepository diameterRepo, CommonVariablesRepository commonVariablesRepo) {
        this.userService = userService;
        this.interfaceVersionsRepository = interfaceVersionsRepository;
		this.bindStatusesRepository = bindStatusesRepository;
		this.npiRepo = npiRepo;
		this.tonRepo = tonRepo;
		this.balanceTypeRepo = balanceTypeRepo;
		this.deliveryStatusRepo = deliveryStatusRepo;
		this.bindsTypesRepo = bindsTypesRepo;
		this.functionalityRepo = functionalityRepo;
		this.slsRangeRepo = slsRangeRepo;
		this.trafficModeRepo = trafficModeRepo;
		this.sccpRuleTypeRepo = sccpRuleTypeRepo;
		this.originationTypeRepo = originationTypeRepo;
		this.loadSharingAlgorithmRepo = loadSharingAlgorithmRepo;
		this.numberingPlanRepo = numberingPlanRepo;
		this.natureOfAddressRepo = natureOfAddressRepo;
		this.encodingTypeRepo = encodingTypeRepo;
		this.generalSettingsHttpSmppRepo = generalSettingsHttpSmppRepo;
		this.generalSettingsMapper = generalSettingsMapper;
		this.utilsBase = utilsBase;
		this.generalSmscRetryRepo = generalSmscRetryRepo;
		this.gtIndicatorRepo = gtIndicatorRepo;
		this.appRepo = appRepo;
		this.processDiameter = processDiameter;
		this.diameterRepo = diameterRepo;
		this.commonVariablesRepo = commonVariablesRepo;
	}

	@PostConstruct
    public void init() {
		userService.createRootUser();

		// interface_versions table
		ArrayList<InterfazVersions> interfazValues = new ArrayList<>();
		
		interfazValues.add(new InterfazVersions("IF_33", "3.3"));
		interfazValues.add(new InterfazVersions("IF_34", "3.4"));
		interfazValues.add(new InterfazVersions("IF_50", "5.0"));
		
		interfaceVersionsRepository.saveAll(interfazValues);
		
		// bind_statuses table
		ArrayList<BindStatuses> bindValues = new ArrayList<>();
		
		bindValues.add(new BindStatuses("STOPPED"));
		bindValues.add(new BindStatuses("STARTED"));
		bindValues.add(new BindStatuses("BINDING"));
		bindValues.add(new BindStatuses("BOUND"));
		bindValues.add(new BindStatuses("UNBINDING"));
		bindValues.add(new BindStatuses("UNBOUND"));
		
		bindStatusesRepository.saveAll(bindValues);
		
		// npi_catalog table
		ArrayList<NpiCatalog> npiValues = new ArrayList<>();
		npiValues.add(new NpiCatalog(-1, "Default"));
		npiValues.add(new NpiCatalog(0, "Unknown"));
		npiValues.add(new NpiCatalog(1, "ISDN"));
		npiValues.add(new NpiCatalog(3, "Data"));
		npiValues.add(new NpiCatalog(4, "Telex"));
		npiValues.add(new NpiCatalog(6, "Land Mobile"));
		npiValues.add(new NpiCatalog(8, "National"));
		npiValues.add(new NpiCatalog(9, "Private"));
		npiValues.add(new NpiCatalog(10, "ERMES"));
		npiValues.add(new NpiCatalog(14, "Internet (IP)"));
		npiValues.add(new NpiCatalog(18, "WAP"));

		npiRepo.saveAll(npiValues);
		
		// ton_catalog table
		ArrayList<TonCatalog> tonValues = new ArrayList<>();
		tonValues.add(new TonCatalog(-1, "Default"));
		tonValues.add(new TonCatalog(0, "Unknown"));
		tonValues.add(new TonCatalog(1, "International"));
		tonValues.add(new TonCatalog(2, "National"));
		tonValues.add(new TonCatalog(3, "Network Specific"));
		tonValues.add(new TonCatalog(4, "Subscriber Number"));
		tonValues.add(new TonCatalog(5, "Alphanumeric"));
		tonValues.add(new TonCatalog(6, "Abbreviated"));
		
		tonRepo.saveAll(tonValues);
		
		ArrayList<BalanceType> balanceTypeValues = new ArrayList<>();
		
		balanceTypeValues.add(new BalanceType("PREPAID"));
		balanceTypeValues.add(new BalanceType("POSTPAID"));
		
		balanceTypeRepo.saveAll(balanceTypeValues);
		
		ArrayList<DeliveryStatus> deliveryStatus = new ArrayList<>();
		deliveryStatus.add(new DeliveryStatus("ENROUTE", "ENROUTE"));
		deliveryStatus.add(new DeliveryStatus("DELIVRD", "DELIVERED"));
		deliveryStatus.add(new DeliveryStatus("EXPIRED", "EXPIRED"));
		deliveryStatus.add(new DeliveryStatus("DELETED", "DELETED"));
		deliveryStatus.add(new DeliveryStatus("UNDELIV", "UNDELIVERED"));
		deliveryStatus.add(new DeliveryStatus("ACCEPTD", "ACCEPTED"));
		deliveryStatus.add(new DeliveryStatus("UNKNOWN", "UNKNOWN"));
		deliveryStatus.add(new DeliveryStatus("REJECTD", "REJECTED"));
		
		deliveryStatusRepo.saveAll(deliveryStatus);
		
		// bind_statuses table
		ArrayList<BindsTypes> bindsTypes = new ArrayList<>();
		
		bindsTypes.add(new BindsTypes("TRANSMITTER"));
		bindsTypes.add(new BindsTypes("RECEIVER"));
		bindsTypes.add(new BindsTypes("TRANSCEIVER"));
		
		bindsTypesRepo.saveAll(bindsTypes);
		
		// ss7
		ArrayList<Functionality> functionalities = new ArrayList<>();
		
		functionalities.add(new Functionality("SGW", "SGW"));
		functionalities.add(new Functionality("AS", "AS"));
		functionalities.add(new Functionality("IPSP-CLIENT", "IPSP Client"));
		functionalities.add(new Functionality("IPSP-SERVER", "IPSP Server"));
		
		functionalityRepo.saveAll(functionalities);
		
		ArrayList<TrafficMode> trafficMode = new ArrayList<>();
		
		trafficMode.add(new TrafficMode(1, "Override"));
		trafficMode.add(new TrafficMode(2, "Loadshare"));
		trafficMode.add(new TrafficMode(3, "Broadcast"));
		
		trafficModeRepo.saveAll(trafficMode);
		
		ArrayList<RuleType> ruleTypes = new ArrayList<>();
		
		ruleTypes.add(new RuleType(1, "Solitary"));
		ruleTypes.add(new RuleType(2, "Dominant"));
		ruleTypes.add(new RuleType(3, "Loadshared"));
		ruleTypes.add(new RuleType(4, "Broadcast"));
		
		sccpRuleTypeRepo.saveAll(ruleTypes);
		
		ArrayList<OriginationType> originationType = new ArrayList<>();
		
		originationType.add(new OriginationType(1, "All"));
		originationType.add(new OriginationType(2, "LocalOriginated"));
		originationType.add(new OriginationType(3, "RemoteOriginated"));
		
		originationTypeRepo.saveAll(originationType);
		
		ArrayList<LoadSharingAlgorithm> loadSharingAlgorithm = new ArrayList<>();
		
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(1, "UNDEFINED"));
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(2, "Bit0"));
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(3, "Bit1"));
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(4, "Bit2"));
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(5, "Bit3"));
		loadSharingAlgorithm.add(new LoadSharingAlgorithm(6, "Bit4"));
		
		loadSharingAlgorithmRepo.saveAll(loadSharingAlgorithm);

		ArrayList<SlsRange> slsRanges = new ArrayList<>();

		slsRanges.add(new SlsRange("All", "All"));
		slsRanges.add(new SlsRange("Odd", "Odd"));
		slsRanges.add(new SlsRange("Even", "Even"));

		slsRangeRepo.saveAll(slsRanges);
		
		ArrayList<NumberingPlan> numbersPlan = new ArrayList<>();
		
		numbersPlan.add(new NumberingPlan(-1, "UNDEFINED"));
		numbersPlan.add(new NumberingPlan(0, "unknown"));
		numbersPlan.add(new NumberingPlan(1, "ISDN"));
		numbersPlan.add(new NumberingPlan(2, "spare_2"));
		numbersPlan.add(new NumberingPlan(3, "data"));
		numbersPlan.add(new NumberingPlan(4, "telex"));
		numbersPlan.add(new NumberingPlan(5, "spare_5"));
		numbersPlan.add(new NumberingPlan(6, "land_mobile"));
		numbersPlan.add(new NumberingPlan(7, "spare_7"));
		numbersPlan.add(new NumberingPlan(8, "national"));
		numbersPlan.add(new NumberingPlan(9, "private_plan"));
		numbersPlan.add(new NumberingPlan(15, "reserved"));
		
		numberingPlanRepo.saveAll(numbersPlan);
		
		ArrayList<NatureOfAddress> natureOfAddresses = new ArrayList<>();
		String natureOfAddressStr = "UNDEFINED, UNKNOWN, SUBSCRIBER, RESERVED_NATIONAL_2, NATIONAL, INTERNATIONAL, SPARE_5, SPARE_6, SPARE_7, SPARE_8, SPARE_9,"
				+ "SPARE_10, SPARE_11, SPARE_12, SPARE_13, SPARE_14, SPARE_15, SPARE_16, SPARE_17, SPARE_18, SPARE_19, SPARE_20, SPARE_21, SPARE_22,"
				+ "SPARE_23, SPARE_24, SPARE_25, SPARE_26, SPARE_27, SPARE_28, SPARE_29, SPARE_30, SPARE_31, SPARE_32, SPARE_33, SPARE_34, SPARE_35,"
				+ "SPARE_36, SPARE_37, SPARE_38, SPARE_39, SPARE_40, SPARE_41, SPARE_42, SPARE_43, SPARE_44, SPARE_45, SPARE_46, SPARE_47, SPARE_48, "
				+ "SPARE_49, SPARE_50, SPARE_51, SPARE_52, SPARE_53, SPARE_54, SPARE_55, SPARE_56, SPARE_57, SPARE_58, SPARE_59, SPARE_60, SPARE_61, "
				+ "SPARE_62, SPARE_63, SPARE_64, SPARE_65, SPARE_66, SPARE_67, SPARE_68, SPARE_69, SPARE_70, SPARE_71, SPARE_72, SPARE_73, SPARE_74, "
				+ "SPARE_75, SPARE_76, SPARE_77, SPARE_78, SPARE_79, SPARE_80, SPARE_81, SPARE_82, SPARE_83, SPARE_84, SPARE_85, SPARE_86, SPARE_87, "
				+ "SPARE_88, SPARE_89, SPARE_90, SPARE_91, SPARE_92, SPARE_93, SPARE_94, SPARE_95, SPARE_96, SPARE_97, SPARE_98, SPARE_99, SPARE_100, "
				+ "SPARE_101, SPARE_102, SPARE_103, SPARE_104, SPARE_105, SPARE_106, SPARE_107, SPARE_108, SPARE_109, SPARE_110, SPARE_111, "
				+ "RESERVED_NATIONAL_112, RESERVED_NATIONAL_113, RESERVED_NATIONAL_114, RESERVED_NATIONAL_115, RESERVED_NATIONAL_116, RESERVED_NATIONAL_117, "
				+ "RESERVED_NATIONAL_118, RESERVED_NATIONAL_119, RESERVED_NATIONAL_120, RESERVED_NATIONAL_121, RESERVED_NATIONAL_122, RESERVED_NATIONAL_123, "
				+ "RESERVED_NATIONAL_124, RESERVED_NATIONAL_125, RESERVED_NATIONAL_126, RESERVED";
		
		String[] natureOfAddressArray = natureOfAddressStr.trim().split(",");
		int natureId = -1;
		for (String address : natureOfAddressArray) {
			natureOfAddresses.add(new NatureOfAddress(natureId, address));
			natureId++;
		}
		natureOfAddressRepo.saveAll(natureOfAddresses);
		
		// general settings
		ArrayList<EncodingType> encodingType = new ArrayList<>();
		encodingType.add(new EncodingType(0, "GSM7"));
		encodingType.add(new EncodingType(1, "UTF8"));
		encodingType.add(new EncodingType(2, "UNICODE"));
		encodingType.add(new EncodingType(3, "ISO88591"));
		encodingTypeRepo.saveAll(encodingType);
		
		GeneralSettingsSmppHttp generalSettingsEntity = generalSettingsHttpSmppRepo.findById(1);
		GeneralSettingsSmppHttpDTO generalSettingSmppHttp = new GeneralSettingsSmppHttpDTO();
		if (generalSettingsEntity == null) {
			generalSettingsEntity = generalSettingsMapper.toEntity(generalSettingSmppHttp);
			generalSettingsHttpSmppRepo.save(generalSettingsEntity);
		}
		generalSettingSmppHttp = generalSettingsMapper.toDTO(generalSettingsEntity);
		utilsBase.storeInRedis(hashTableGeneralSettings, hashKeyGeneralSettingsSmppHttp, generalSettingSmppHttp.toString());
		utilsBase.sendNotificationSocket(GENERAL_SETTINGS_SMPP_HTTP_ENDPOINT, "updated");
		
		GeneralSmscRetry generalSmscRetryEntity = generalSmscRetryRepo.findById(1);
		GeneralSmscRetryDTO generalSmscRetry =  new GeneralSmscRetryDTO();
		if (generalSmscRetryEntity == null) {
			generalSmscRetryEntity = generalSettingsMapper.toEntitySmscRetry(generalSmscRetry);
			generalSmscRetryRepo.save(generalSmscRetryEntity);
		}
		generalSmscRetry = generalSettingsMapper.toDTOSmscRetry(generalSmscRetryEntity);
		utilsBase.storeInRedis(hashTableGeneralSettings, hashKeyGeneralSettingsSmscRetry, generalSmscRetry.toString());
		utilsBase.sendNotificationSocket(GENERAL_SMSC_RETRY_ENDPOINT, "updated");
		
		ArrayList<GlobalTitleIndicator> gtIndicators = new ArrayList<>();

		gtIndicators.add(new GlobalTitleIndicator("GT0001", "GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY"));
		gtIndicators.add(new GlobalTitleIndicator("GT0010", "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY"));
		gtIndicators.add(new GlobalTitleIndicator("GT0011", "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME"));
		gtIndicators.add(new GlobalTitleIndicator("GT0100", "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS"));

		gtIndicatorRepo.saveAll(gtIndicators);
		
		ArrayList<Applications> applications = new ArrayList<>();
		applications.add(new Applications(1, "Diameter Credit Control", 0, 4, 0, "OCS"));
		applications.add(new Applications(2, "16777238 3GPP Gx 29.212", 10415, 16777238, 0, "PCRF"));
		applications.add(new Applications(3, "16777224 3GPP Gx 29.210", 10415, 16777224, 0, "PCRF"));
		applications.add(new Applications(4, "16777225 3GPP Gx over Gy 29.210", 10415, 16777225, 0, "PCRF"));
		
		appRepo.saveAll(applications);
		
		try {
			Diameter diameter = diameterRepo.findByEnabled(ENABLED_STATUS);
			if (diameter != null) {
				processDiameter.updateOrCreateJsonInRedis(diameter.getId(), true);
			}
		} catch (Exception e) {
			log.error("Error to load diameter configurations to Redis -> {}", e.getMessage());
		}
		
		// Local charging
		CommonVariables commonVar = commonVariablesRepo.findByKey(LOCAL_CHARGING);
		if (Objects.isNull(commonVar)) {
			commonVar = new CommonVariables();
			commonVar.setDataType("boolean");
			commonVar.setKey(LOCAL_CHARGING);
			commonVar.setValue("true");
			
			commonVariablesRepo.save(commonVar);
		}
		commonVar = commonVariablesRepo.findByKey(SMSC_ACCOUNT_SETTINGS);
		if (Objects.isNull(commonVar)) {
			commonVar = new CommonVariables();
			commonVar.setDataType("json");
			commonVar.setKey(SMSC_ACCOUNT_SETTINGS);
			commonVar.setValue("{\"" + KEY_MAX_PASSWORD_LENGTH +"\": 9, \"" + KEY_MAX_SYSTEM_ID_LENGTH + "\": 15}");

			commonVariablesRepo.save(commonVar);
		}
    }
}
