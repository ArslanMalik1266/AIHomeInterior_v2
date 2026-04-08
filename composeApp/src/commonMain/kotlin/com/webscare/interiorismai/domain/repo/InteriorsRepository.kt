package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.domain.model.InteriorColors
import com.webscare.interiorismai.domain.model.InteriorStyles
import com.webscare.interiorismai.domain.model.InteriorTypes

interface InteriorsRepository {
    suspend fun getStyles(): InteriorStyles
    suspend fun getTypes(): InteriorTypes
    suspend fun getColors(): InteriorColors
}