/**
 * WRML - Web Resource Modeling Language
 *  __     __   ______   __    __   __
 * /\ \  _ \ \ /\  == \ /\ "-./  \ /\ \
 * \ \ \/ ".\ \\ \  __< \ \ \-./\ \\ \ \____
 *  \ \__/".~\_\\ \_\ \_\\ \_\ \ \_\\ \_____\
 *   \/_/   \/_/ \/_/ /_/ \/_/  \/_/ \/_____/
 *
 * http://www.wrml.org
 *
 * Copyright (C) 2011 - 2013 Mark Masse <mark@wrml.org> (OSS project WRML.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wrml.runtime.format;

import org.wrml.model.Model;

import java.io.IOException;
import java.util.List;

public interface ModelPrinter {

    void close() throws IOException, ModelPrinterException;

    ModelWriteOptions getWriteOptions();

    void printBooleanValue(boolean booleanValue) throws IOException, ModelPrinterException;

    void printDoubleValue(double value) throws IOException, ModelPrinterException;

    void printIntegerValue(int value) throws IOException, ModelPrinterException;

    void printListEnd(List<?> list) throws IOException, ModelPrinterException;

    void printListStart(List<?> list) throws IOException, ModelPrinterException;

    void printLongValue(long value) throws IOException, ModelPrinterException;

    void printModelEnd(Model model) throws IOException, ModelPrinterException;

    void printModelStart(Model model) throws IOException, ModelPrinterException;

    void printNullValue() throws IOException, ModelPrinterException;

    void printSlotName(String slotName) throws IOException, ModelPrinterException;

    void printTextValue(String dateString) throws IOException, ModelPrinterException;
}
