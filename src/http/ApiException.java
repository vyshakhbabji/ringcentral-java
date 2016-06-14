package http;

/*
 * Copyright (c) 2015 RingCentral, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import core.RingCentralException;

/**
 * This is a RingCentral APIException Class
 */
public class ApiException extends RingCentralException {
	public enum ErrorType {
		Client, Service, Unknown
	}

	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMessage;
	private ErrorType errorType = ErrorType.Unknown;
	private String extraInfo;
	private String message;
	private String rawResponseContent;
	private Request request;

	private ApiResponse response;
	private String serviceName;

	private int statusCode;

	public ApiException(ApiResponse response, Exception cause) {
		super(null, cause);
		this.response = response;
		this.extraInfo = response.error();
	}

	public ApiException(Exception cause) {
		super(null, cause);
	}

	public ApiException(Response response) {
		super((String) null);
	}

	public ApiException(String errorMessage) {
		super((String) null);
		this.errorMessage = errorMessage;
	}

	public ApiException(String errorMessage, Exception cause) {
		super(null, cause);
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public ErrorType getErrorType() {
		return errorType;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	@Override
	public String getMessage() {
		String errormessage = getErrorMessage() + " (Service: "
				+ getServiceName() + "; " + "; Error Code: " + getErrorCode()
				+ ")";
		return extraInfo == "" ? errormessage : errormessage + extraInfo;
	}

	public String getRawResponseContent() {
		return rawResponseContent;
	}

	public String getServiceName() {
		return serviceName;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String value) {
		errorMessage = value;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public void setRawResponseContent(String rawResponseContent) {
		this.rawResponseContent = rawResponseContent;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
