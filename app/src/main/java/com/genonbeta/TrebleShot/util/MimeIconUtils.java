package com.genonbeta.TrebleShot.util;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modified-by: veli
 * Date: 16/11/2018 18:57
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.DocumentsContract;

import androidx.core.content.ContextCompat;

import com.genonbeta.TrebleShot.R;

import java.util.HashMap;

public class MimeIconUtils
{
    private static HashMap<String, Integer> sMimeIcons = new HashMap<>();

    static {
        int icon;
        // Package
        icon = R.drawable.ic_android_head_white_24dp;
        add("application/vnd.android.package-archive", icon);
        // Certificate
        icon = R.drawable.ic_certificate_white_24dp;
        add("application/pgp-keys", icon);
        add("application/pgp-signature", icon);
        add("application/x-pkcs12", icon);
        add("application/x-pkcs7-certreqresp", icon);
        add("application/x-pkcs7-crl", icon);
        add("application/x-x509-ca-cert", icon);
        add("application/x-x509-user-cert", icon);
        add("application/x-pkcs7-certificates", icon);
        add("application/x-pkcs7-mime", icon);
        add("application/x-pkcs7-signature", icon);
        // Image
        icon = R.drawable.ic_photo_white_24dp;
        add("application/vnd.oasis.opendocument.graphics", icon);
        add("application/vnd.oasis.opendocument.graphics-template", icon);
        add("application/vnd.oasis.opendocument.image", icon);
        add("application/vnd.stardivision.draw", icon);
        add("application/vnd.sun.xml.draw", icon);
        add("application/vnd.sun.xml.draw.template", icon);
        // PDF
        icon = R.drawable.ic_file_pdf_box_white_24dp;
        add("application/pdf", icon);
        // Presentation
        icon = R.drawable.ic_file_presentation_box_white_24dp;
        add("application/vnd.stardivision.impress", icon);
        add("application/vnd.sun.xml.impress", icon);
        add("application/vnd.sun.xml.impress.template", icon);
        add("application/x-kpresenter", icon);
        add("application/vnd.oasis.opendocument.presentation", icon);
        // Spreadsheet
        icon = R.drawable.ic_google_spreadsheet_white_24dp;
        add("application/vnd.oasis.opendocument.spreadsheet", icon);
        add("application/vnd.oasis.opendocument.spreadsheet-template", icon);
        add("application/vnd.stardivision.calc", icon);
        add("application/vnd.sun.xml.calc", icon);
        add("application/vnd.sun.xml.calc.template", icon);
        add("application/x-kspread", icon);
        // Document
        icon = R.drawable.ic_file_document_box_white_24dp;
        add("application/vnd.oasis.opendocument.text", icon);
        add("application/vnd.oasis.opendocument.text-master", icon);
        add("application/vnd.oasis.opendocument.text-template", icon);
        add("application/vnd.oasis.opendocument.text-web", icon);
        add("application/vnd.stardivision.writer", icon);
        add("application/vnd.stardivision.writer-global", icon);
        add("application/vnd.sun.xml.writer", icon);
        add("application/vnd.sun.xml.writer.global", icon);
        add("application/vnd.sun.xml.writer.template", icon);
        add("application/x-abiword", icon);
        add("application/x-kword", icon);
        // Video
        icon = R.drawable.ic_video_white_24dp;
        add("application/x-quicktimeplayer", icon);
        add("application/x-shockwave-flash", icon);
        // Word
        icon = R.drawable.ic_file_word_box_white_24dp;
        add("application/msword", icon);
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.document", icon);
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.template", icon);
        // Excel
        icon = R.drawable.ic_file_excel_box_white_24dp;
        add("application/vnd.ms-excel", icon);
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", icon);
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.template", icon);
        // Powerpoint
        icon = R.drawable.ic_file_powerpoint_box_24dp;
        add("application/vnd.ms-powerpoint", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.presentation", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.template", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.slideshow", icon);
    }

    private static void add(String mimeType, int resId)
    {
        if (sMimeIcons.put(mimeType, resId) != null) {
            throw new RuntimeException(mimeType + " already registered!");
        }
    }

    public static Drawable loadMimeIcon(Context context, String mimeType)
    {
        return ContextCompat.getDrawable(context, loadMimeIcon(mimeType));
    }

    public static int loadMimeIcon(String mimeType)
    {
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType))
            return R.drawable.ic_folder_white_24dp;

        Integer resId = sMimeIcons.get(mimeType);

        if (resId != null)
            return resId;

        if (mimeType == null)
            return R.drawable.ic_insert_drive_file_white_24dp;

        final String typeOnly = mimeType.split("/")[0];

        if ("image".equals(typeOnly)) {
            return R.drawable.ic_photo_white_24dp;
        } else if ("text".equals(typeOnly)) {
            return R.drawable.ic_file_document_box_white_24dp;
        } else if ("video".equals(typeOnly)) {
            return R.drawable.ic_video_white_24dp;
        } else {
            return R.drawable.ic_insert_drive_file_white_24dp;
        }
    }
}