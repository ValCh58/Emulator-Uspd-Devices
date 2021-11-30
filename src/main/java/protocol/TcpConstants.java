package protocol;

public interface TcpConstants {

	public static interface RecvPacketRuleConstants {

		/** Тип правила получения: фиксированная длина */
		public static final String TYPE_FIXLENGTH = "fixLength";
		/** Тип правила получения пакета: определение длины */
		public static final String TYPE_LENGTHIDENTIFY = "lengthIdentify";
		/** Тип правила получения: Терминаторный символ */
		public static final String TYPE_ENDCHAR = "endChar";
        /** Наша строка */        
        public static final String TYPE_ENDCHAR_2 = "endChar2";//!!!
                
		/** Тип правила получения: Пользовательский */
		public static final String TYPE_CUSTOM = "custom";

		public static final String TYPE_CUSTOMEX = "customex";
	}

	public static interface HeartBeatRuleConstants {

		/** Тип правила содержимого пакета: порядок символов */
		public static final String TYPE_CHARS = "chars";
		/** Тип правила содержимого пакета: порядок байтов */
		public static final String TYPE_BYTES = "bytes";
	}

	public static interface LengthIdentifyConstants {

		/** Идентификатор длины Тип поля: Порядок символов */
		public static final String VALTYPE_CHARS = "chars";
		/** Тип поля идентификации длины: порядок сетевых байтов */
		public static final String VALTYPE_BYTES = "bytes";
		/** Тип поля идентификации длины: обратный сетевой порядок байтов */
		public static final String VALTYPE_OBYTES = "obytes";

		/** Поле идентификатора длины Тип длины: код символа */
		public static final String FILLTYPE_CHAR = "char";
		/** Поле идентификатора длины Тип длины: байт-код */
		public static final String FILLTYPE_BYTE = "byte";

		/** Направление заполнения поля идентификации длины: отступ слевa */
		public static final String FILLLOCATION_LEFT = "left";
		/** Направление заполнения поля идентификации длины: правое заполнение */
		public static final String FILLLOCATION_RIGHT = "right";
	}

	public static interface ObjectSerialConstants {

		/** Пакет поддерживает максимальный размер объекта */
		public static final int ENCODE_MAX_SIZE = 1 << 20;
		/** Распаковка поддерживает максимальный размер объекта */
		public static final int DECODE_MAX_SIZE = 1 << 20;
	}

}
