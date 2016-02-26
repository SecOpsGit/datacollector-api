/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.api;

import java.util.Set;

/**
 * A <code>Record</code> represents the unit of data Data Collector pipelines process. A record has a {@link Field}
 * representing the data as well as a header for metadata information.
 * <p/>
 * The {@link Header} of a <code>Record</code> is the associated metadata, some of which is generated by the Pipeline
 * runtime container.
 * <p/>
 * The <code>Record</code> API supports a field-path expressions (a simplified
 * <a href="http://en.wikipedia.org/wiki/XPath">XPath</a> like syntax) to directly access values within a
 * <code>Field</code> value data structure:
 * <ul>
 *   <li>An empty string <code>""</code> refers to the root element of the <code>Record</code></li>
 *   <li>A <code>/{NAME}NAME</code> string refers to the '{NAME}' entry in a <code>Map</code> or a
 *       <code>LIST_MAP</code></li>
 *   <li>A <code>[{INDEX}]</code>, with {INDEX} being a zero/positive integer, refers to the '{INDEX}' position in a
 *       <code>List</code> or a <code>LIST_MAP</code></li>
 * </ul>
 * A field-path expression can be used to reference a composite data structure of any depth and composition, for
 * example:
 * <ul>
 *   <li>/contactInfo/firstName</li>
 *   <li>/contactInfo/lastName</li>
 *   <li>/contactInfo/email[[0]]</li>
 *   <li>/contactInfo/address/firstLine</li>
 *   <li>/contactInfo/address/secondLine</li>
 *   <li>/contactInfo/address/city</li>
 *   <li>/contactInfo/address/state</li>
 *   <li>/contactInfo/address/zip</li>
 *   <li>/contactInfo/phone[[0]]/number</li>
 *   <li>/contactInfo/phone[[0]]/type</li>
 *   <li>/contactInfo/phone[[1]]/number</li>
 *   <li>/contactInfo/phone[[1]]/type</li>
 * </ul>
 * Using field-path expressions is possible to check for existence, access, modify and delete a <code>Field</code> data
 * structure. The {@link #get(String)}, {@link #has(String)}, {@link #delete(String)} and {@link #getEscapedFieldPaths()}
 * methods work using field- path expressions.
 * <p/>
 * <b>IMPORTANT:</b> Map key names that are not a word or use any of the following 3 special
 * characters '<code>/</code>', '<code>[</code>' or '<code>]</code>' must be single-quoted or double-quoted, for
 * example: '<code>'foo bar'/"xyz]"</code>.
 */
public interface Record {

  /**
   * Metadata of the record.
   * <p/>
   * Some of the information in the header is populated by the Data Collector.
   */
  public interface Header {

    /**
     * Returns the name of the stage instance that created the record.
     * <p/>
     * This value is automatically set by the Data Collector.
     *
     * @return the name of the stage instance that created the record.
     */
    public String getStageCreator();

    public String getSourceId();

    /**
     * Returns the tracking ID for the record. Primary used by the UI.
     * <p/>
     * This value is automatically set by the Data Collector.
     *
     * @return the tracking ID for the record.
     */
    public String getTrackingId();

    /**
     * Returns the previous tracking ID for the record. Primary used by the UI.
     * <p/>
     * This value is automatically set by the Data Collector.
     *
     * @return the previous tracking ID for the record.
     */
    public String getPreviousTrackingId();

    /**
     * Returns the stages the record went through.
     * <p/>
     * Teh stages instance names are separated by ':'.
     *
     * @return the stages the record went through.
     */
    public String getStagesPath();

    /**
     * Return the original origin raw bytes for the record if available.
     *
     * @return the original origin raw bytes for the record if available, <code>NULL</code> otherwise.
     */
    public byte[] getRaw();

    /**
     * Return the MIME type of the original origin raw bytes for the record if available.
     *
     * @return the MIME type of the original origin raw bytes for the record if available, <code>NULL</code> otherwise.
     */
    public String getRawMimeType();

    /**
     * Returns the list of user defined attribute names.
     *
     * @return the list of user defined attribute names, if there are none it returns an empty list.
     */
    public Set<String> getAttributeNames();

    /**
     * Returns the value of the specified attribute.
     *
     * @param name attribute name.
     * @return the value of the specified attribute, or <code>NULL</code> if the attribute does not exist.
     */
    public String getAttribute(String name);

    /**
     * Sets an attribute.
     *
     * @param name attribute name.
     * @param value attribute value, it cannot be <code>NULL</code>.
     */
    public void setAttribute(String name, String value);

    /**
     * Deletes an attribute.
     *
     * @param name the attribute to delete.
     */
    public void deleteAttribute(String name);

    /**
     * If the record has been sent to error, it returns the UUID of the data collector that sent the record to error.
     *
     * @return the UUID of the data collector that sent the record to error, <code>NULL</code> if the record has not
     * been sent to error.
     */
    public String getErrorDataCollectorId();

    /**
     * If the record has been sent to error, it returns the name of the pipeline that sent the record to error.
     *
     * @return the pipeline that sent the record to error, <code>NULL</code> if the record has not been sent to error.
     */
    public String getErrorPipelineName();

    /**
     * If the record has been sent to error, it returns the error code.
     *
     * @return the error code, <code>NULL</code> if the record has not been sent to error.
     */
    public String getErrorCode();

    /**
     * If the record has been sent to error, it returns the error message.
     *
     * @return the error message, <code>NULL</code> if the record has not been sent to error.
     */
    public String getErrorMessage();

    /**
     * If the record has been sent to error, it returns the stage instance name that sent the message to error.
     *
     * @return the stage instance name that sent the message to error, <code>NULL</code> if the record has not been
     * sent to error.
     */
    public String getErrorStage();

    /**
     * If the record has been sent to error, it returns the timestamp (UNIX Epoch time) when the record was sent to
     * error.
     *
     * @return the timestamp (UNIX Epoch time) when the record was sent to error, <code>NULL</code> if the record has
     * not been sent to error.
     */
    public long getErrorTimestamp();

  }

  /**
   * Returns the metadata header of the record.
   *
   * @return the metadata header of the record.
   */
  public Header getHeader();

  /**
   * Returns the root data field of the record.
   *
   * @return the root data field of the record.
   */
  public Field get();

  /**
   * Sets the root data field of the record.
   *
   * @param field the field to set.
   *
   * @return the old field value, or <code>NULL</code> if there was none.
   */
  public Field set(Field field);

  /**
   * Returns the <code>Field</code> at the specified field-path.
   *
   * @param fieldPath field-path of the <code>Field</code> to retrieve.
   * @return the <code>Field</code> at the specified field-path, or <code>NULL</code> if none.
   *
   * @see Field
   */
  public Field get(String fieldPath);

  /**
   * Deletes the <code>Field</code> at the specified field-path.
   *
   * @param fieldPath field-path of the <code>Field</code> to delete.
   * @return the deleted <code>Field</code>, or <code>NULL</code> if none.
   *
   * @see Field
   */
  public Field delete(String fieldPath);

  /**
   * Indicates if the specified field-path has a <code>Field</code> or not.
   *
   * @param fieldPath field-path of the <code>Field</code> to check for existence.
   * @return if the specified field-path has a <code>Field</code> or not.
   */
  public boolean has(String fieldPath);

  /**
   * Returns all available field-paths in the record. This method is deprecated, and {@link #getEscapedFieldPaths()}
   * should be used instead.
   *
   * @return all available field-paths in the record.
   */
  @Deprecated
  public Set<String> getFieldPaths();

  /**
   * Returns all available field-paths in the record. Field-paths that contain any non-word characters are escaped by
   * single quotes.
   *
   * @return all available field-paths in the record.
   *
   * @since 1.2.2.0
   */
  public Set<String> getEscapedFieldPaths();

  /**
   * Sets a <code>Field</code> in the specified field-path.
   * <p/>
   * The field-path parent must exist.
   *
   * @param fieldPath the field-path to set the <code>Field</code>.
   * @param newField the <code>Field</code> to set.
   * @return the <code>Field</code> being replaced at the specified field-path, or <code>NULL</code> if none.
   */
  public Field set(String fieldPath, Field newField);

}
