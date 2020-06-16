/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.pinterest.secor.parser;

import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
/**
 * JsonMessageParser extracts timestamp field (specified by 'message.timestamp.name')
 * from JSON data and partitions data by date.
 */
public class FileBeatParser extends TimestampedMessageParser {
    private final boolean m_timestampRequired;

    public FileBeatParser(SecorConfig config) {
        super(config);
        m_timestampRequired = config.isMessageTimestampRequired();
    }

    @Override
    public long extractTimestampMillis(final Message message) {
        String fieldValue = null;
        JSONParser parser = new JSONParser();

        try {
            String payload = new String(message.getPayload());
            JSONObject jsonObject = (JSONObject) parser.parse(payload);
            fieldValue = (String) jsonObject.get("@timestamp");
        } catch (ParseException e) {
            String payload = new String(message.getPayload());
            throw new RuntimeException("Cannot parse payload: " + payload);
        }
        
        try {
            Date dateFormat = DatatypeConverter.parseDateTime(fieldValue).getTime();
            return dateFormat.getTime();
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Bad timestamp field for message: " + message);
        }

    }
}