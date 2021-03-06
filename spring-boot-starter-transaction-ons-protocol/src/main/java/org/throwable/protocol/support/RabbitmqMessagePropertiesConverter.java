package org.throwable.protocol.support;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.MediaType;
import org.throwable.ons.core.common.constants.Constants;
import org.throwable.protocol.constants.LocalTransactionStats;
import org.throwable.protocol.constants.SendStats;
import org.throwable.protocol.model.DestinationMessagePair;
import org.throwable.protocol.model.MessageBody;
import org.throwable.protocol.support.converter.FireTransactionStatsConverter;
import org.throwable.protocol.support.converter.LocalTransactionStatsConverter;
import org.throwable.protocol.support.converter.SendStatsConverter;

import java.util.Date;
import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/20 17:33
 */
public final class RabbitmqMessagePropertiesConverter {

	private static final MessagePropertiesConverter MESSAGE_PROPERTIES_CONVERTER = new DefaultMessagePropertiesConverter();
	private static final DefaultConversionService CONVERSION_SERVICE = new DefaultConversionService();

	static {
		CONVERSION_SERVICE.addConverter(new FireTransactionStatsConverter());
		CONVERSION_SERVICE.addConverter(new LocalTransactionStatsConverter());
		CONVERSION_SERVICE.addConverter(new SendStatsConverter());
	}

	public AMQP.BasicProperties createMessagePropertiesAndConvertToBasicProperties() {
		return convertToBasicProperties(createMessageProperties());
	}

	public MessageProperties createMessageProperties() {
		MessagePropertiesBuilder builder = MessagePropertiesBuilder.newInstance();
		builder.setTimestamp(new Date());
		builder.setContentEncoding(Constants.ENCODING);
		builder.setContentType(MediaType.APPLICATION_JSON_VALUE);
		return builder.build();
	}

	public AMQP.BasicProperties convertToBasicProperties(MessageProperties messageProperties) {
		return MESSAGE_PROPERTIES_CONVERTER.fromMessageProperties(messageProperties, Constants.ENCODING);
	}

	public byte[] wrapRabbitmqMessageBody(final String messageId,
										  final String uniqueCode,
										  final String checkerClassName,
										  final String transactionId,
										  final LocalTransactionStats localTransactionStats,
										  final SendStats sendStats,
										  final String exchangeRoutingKeySuffix,
										  final List<DestinationMessagePair> destinationMessagePairs) {
		MessageBody messageBody = new MessageBody();
		messageBody.setContents(destinationMessagePairs);
		messageBody.addAttribute(Constants.UNIQUECODE_KEY, uniqueCode);
		messageBody.addAttribute(Constants.MESSAGEID_KEY, messageId);
		messageBody.addAttribute(Constants.CHECKERCLASSNAME_KEY, checkerClassName);
		messageBody.addAttribute(Constants.TRANSACTIONID_KEY, transactionId);
		messageBody.addAttribute(Constants.LOCALTRANSACTIONSTATS_KEY, localTransactionStats);
		messageBody.addAttribute(Constants.SENDSTATS_KEY, sendStats);
		messageBody.addAttribute(Constants.EXCHANGE_ROUTING_KEY_SUFFIX_KEY, exchangeRoutingKeySuffix);
		return messageBody.toRabbitmqMessageBodyBytes();
	}

	public String getAttributeValue(MessageBody messageBody, String key) {
		Object value = messageBody.getAttribute(key);
		if (null == value) {
			return null;
		} else {
			return CONVERSION_SERVICE.convert(value, String.class);
		}
	}

	public String getAttributeValue(MessageBody messageBody, String key, String def) {
		Object value = messageBody.getAttribute(key);
		return null != value ? CONVERSION_SERVICE.convert(value, String.class) : def;

	}

	public <T> T getAttributeValue(MessageBody messageBody, String key, Class<T> clazz) {
		Object value = messageBody.getAttribute(key);
		if (null == value) {
			return null;
		} else {
			return CONVERSION_SERVICE.convert(value, clazz);
		}
	}

	public <T> T getAttributeValue(MessageBody messageBody, String key, Class<T> clazz, T def) {
		Object value = messageBody.getAttribute(key);
		return null != value ? CONVERSION_SERVICE.convert(value, clazz) : def;
	}
}
