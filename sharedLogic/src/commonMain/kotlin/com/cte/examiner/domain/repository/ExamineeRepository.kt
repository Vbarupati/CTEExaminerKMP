package com.cte.examiner.domain.repository

import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo

interface ExamineeRepository {
    /** Looks up an asset/examinee by its qcode. */
    suspend fun getExaminee(qcode: String): Result<Examinee>

    /** Returns the signed-in user's profile/info. */
    suspend fun getUserInfo(): Result<UserInfo>
}