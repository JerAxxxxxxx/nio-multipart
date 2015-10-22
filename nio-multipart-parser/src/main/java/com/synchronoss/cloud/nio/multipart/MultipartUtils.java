package com.synchronoss.cloud.nio.multipart;

import org.apache.commons.fileupload.ParameterParser;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 *     Utilities to parse a multipart request/response.
 * </p>
 * Created by sriz0001 on 12/10/2015.
 */
public class MultipartUtils {

    public static final String MULTIPART = "multipart/";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String CONTENT_TYPE = "Content-type";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";

    private MultipartUtils(){}// empty private constructor

    /**
     * <p>
     *     Checks if the Content-Type header defines a multipart request.
     * </p>
     * @param contentTypeHeaderValue The value of the Content-Type header.
     * @return true if the request is a multipart request, false otherwise.
     */
    public static boolean isMultipart(final String contentTypeHeaderValue){
        return contentTypeHeaderValue!=null && contentTypeHeaderValue.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART);
    }

    /**
     * <p>
     *     Returns the list of values fo a particular header.
     * </p>
     * @param headerName The header name
     * @param headers The list of headers
     * @return The list of header values or null.
     */
    public static List<String> getHeaders(final String headerName, final Map<String, List<String>> headers){
        return headers.get(headerName.toLowerCase());
    }

    /**
     * <p>
     *     Returns the first value of a particular header
     * </p>
     * @param headerName The header name
     * @param headers The headers
     * @return The first value of the header or null
     */
    public static String getHeader(final String headerName, final Map<String, List<String>> headers){
        List<String> headerValues = getHeaders(headerName, headers);
        if (headerValues == null || headerValues.size() == 0){
            return null;
        }
        return headerValues.get(0);
    }

    /**
     * <p>
     *     Checks if the part is a form field.
     * </p>
     * @param headers The part headers
     * @return true if the part is a form field, false otherwise.
     */
    public static boolean isFormField(final Map<String, List<String>> headers){
        final String fileName = getFileName(headers);
        final String fieldName = getFieldName(headers);

        return fieldName != null && fileName == null;
    }

    /**
     * <p>
     *     Returns the 'filename' parameter of the Content-disposition header.
     * </p>
     * @param headers The list of headers
     * @return The 'filename' parameter of the Content-disposition header or null
     */
    public static String getFileName(final Map<String, List<String>> headers) {

        final String contentDisposition = getHeader(CONTENT_DISPOSITION, headers);
        String fileName = null;

        if (contentDisposition != null) {
            String contentDispositionLc = contentDisposition.toLowerCase(Locale.ENGLISH);
            if (contentDispositionLc.startsWith(FORM_DATA) || contentDispositionLc.startsWith(ATTACHMENT)) {
                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);// Parameter parser can handle null input
                Map<String, String> params = parser.parse(contentDisposition, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                    } else {
                        // Even if there is no value, the parameter is present, so we return an empty file name rather than no file name.
                        fileName = "";
                    }
                }
            }
        }

        return fileName;
    }

    /**
     * <p>
     *     Returns the 'name' parameter of the Content-disposition header.
     * </p>
     * @param headers The list of headers
     * @return The 'name' parameter of the Content-disposition header or null
     */
    public static String getFieldName(final Map<String, List<String>> headers) {

        final String contentDisposition = getHeader(CONTENT_DISPOSITION, headers);
        String fieldName = null;

        if (contentDisposition != null && contentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true); // Parameter parser can handle null input
            Map<String, String> params = parser.parse(contentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }

        return fieldName;
    }

}