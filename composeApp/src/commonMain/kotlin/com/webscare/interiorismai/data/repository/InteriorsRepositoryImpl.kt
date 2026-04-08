package com.webscare.interiorismai.data.repository

import com.webscare.interiorismai.data.remote.service.RoomService
import com.webscare.interiorismai.domain.model.InteriorColors
import com.webscare.interiorismai.domain.model.InteriorStyles
import com.webscare.interiorismai.domain.model.InteriorTypes
import com.webscare.interiorismai.domain.repo.InteriorsRepository
import io.ktor.client.call.body

class InteriorsRepositoryImpl(
    private val roomService: RoomService
) : InteriorsRepository {

    override suspend fun getStyles(): InteriorStyles =
        roomService.getStyles().body<InteriorStyles>()

    override suspend fun getTypes(): InteriorTypes =
        roomService.getTypes().body<InteriorTypes>()

    override suspend fun getColors(): InteriorColors =
        roomService.getColors().body()
}