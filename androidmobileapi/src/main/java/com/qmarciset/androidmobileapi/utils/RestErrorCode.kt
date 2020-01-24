package com.qmarciset.androidmobileapi.utils

object RestErrorCode {
    // Web
    const val entity_not_found = 1_800
    const val unsupported_format = 1_801
    const val dataset_not_found = 1_802
    const val dataset_not_matching_entitymodel = 1_803
    const val cannot_build_list_of_attribute = 1_804
    const val cannot_build_list_of_attribute_for_expand = 1_805
    const val url_is_malformed = 1_806
    const val ampersand_instead_of_questionmark = 1_807
    const val expecting_closing_single_quote = 1_808
    const val expecting_closing_double_quote = 1_809
    const val wrong_list_of_attribute_to_order_by = 1_810
    const val unknown_rest_query_keyword = 1_811
    const val unknown_rest_method = 1_812
    const val method_not_applicable = 1_813
    const val uag_db_does_not_exist = 1_814
    const val subentityset_cannot_be_applied_here = 1_815
    const val empty_attribute_list = 1_816
    const val compute_action_does_not_exist = 1_817
    const val wrong_logic_operator = 1_818
    const val missing_other_collection_ref = 1_819
    const val wrong_other_collection_ref = 1_820
    const val wrong_transaction_command = 1_821
    const val login_failed = 1_822
    const val max_number_of_sessions_reached = 1_823
    const val unknown_picture_mime_type = 1_824
    const val missing_picture_ref = 1_825
    const val missing_blob_ref = 1_826
    const val method_name_is_unknown = 1_827
    const val limited_alterations = 1_828
    const val method_called_on_a_HTTP_GET = 1_829

    // DB
    const val wrong_comp_operator = 1_112
    const val invalid_query = 1_162
    const val cannot_complete_query = 1_200
    const val cannot_analyze_query = 1_201
    const val cannot_complete_complexquery = 1_203
    const val cannot_analyze_complexquery = 1_204
    const val query_placeholder_is_missing_or_null = 1_279
    const val query_placeholder_wrongtype = 1_280

    // DB entity model
    const val entity_attribute_not_found = 1_500
}
