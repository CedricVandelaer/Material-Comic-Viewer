// ------------------------------------------------------------------------------
// Copyright (c) 2014 Microsoft Corporation
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.live.sample.util;

/**
 * See http://msdn.microsoft.com/en-us/library/hh243646.aspx for more
 * information.
 */
public final class Scopes {

    /* Core Scopes */
    public final static String BASIC = "wl.basic";
    public final static String OFFLINE_ACCESS = "wl.offline_access";
    public final static String SIGNIN = "wl.signin";

    /* Extended Scopes */
    public final static String BIRTHDAY = "wl.birthday";
    public final static String CALENDARS = "wl.calendars";
    public final static String CALENDARS_UPDATE = "wl.calendars_update";
    public final static String CONTACTS_BIRTHDAY = "wl.contacts_birthday";
    public final static String CONTACTS_CREATE = "wl.contacts_create";
    public final static String CONTACTS_CALENDARS = "wl.contacts_calendars";
    public final static String CONTACTS_PHOTOS = "wl.contacts_photos";
    public final static String CONTACTS_SKYDRIVE = "wl.contacts_skydrive";
    public final static String EMAILS = "wl.emails";
    public final static String EVENTS_CREATE = "wl.events_create";
    public final static String PHONE_NUMBERS = "wl.phone_numbers";
    public final static String PHOTOS = "wl.photos";
    public final static String POSTAL_ADDRESSES = "wl.postal_addresses";
    public final static String SHARE = "wl.share";
    public final static String SKYDRIVE = "wl.skydrive";
    public final static String SKYDRIVE_UPDATE = "wl.skydrive_update";
    public final static String WORK_PROFILE = "wl.work_profile";

    /* Developer Scopes */
    public final static String APPLICATIONS = "wl.applications";
    public final static String APPLICATIONS_CREATE = "wl.applications_create";

    public final static String[] ALL = {
        BASIC,
        OFFLINE_ACCESS,
        SIGNIN,
        BIRTHDAY,
        CONTACTS_BIRTHDAY,
        CONTACTS_PHOTOS,
        EMAILS,
        EVENTS_CREATE,
        PHONE_NUMBERS,
        PHOTOS,
        POSTAL_ADDRESSES,
        SHARE,
        WORK_PROFILE,
        APPLICATIONS,
        APPLICATIONS_CREATE
    };

    private Scopes() {
        throw new AssertionError("Unable to create a Scopes object.");
    }
}
