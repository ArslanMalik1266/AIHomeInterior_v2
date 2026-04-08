package com.webscare.interiorismai.di

import org.koin.dsl.module
import com.webscare.interiorismai.data.repository.DraftsRepositoryImpl
import com.webscare.interiorismai.data.repository.GenerateRoomRepositoryImpl
import com.webscare.interiorismai.data.repository.GuestRepositoryImpl
import com.webscare.interiorismai.data.repository.InteriorsRepositoryImpl
import com.webscare.interiorismai.data.repository.RecentGeneratedRepositoryImpl
import com.webscare.interiorismai.domain.repo.DraftsRepository
import com.webscare.interiorismai.domain.repo.GenerateRoomRepository
import com.webscare.interiorismai.domain.repo.GuestRepository
import com.webscare.interiorismai.domain.repo.InteriorsRepository
import com.webscare.interiorismai.domain.repo.RecentGeneratedRepository

val repositoryModule = module {
   single<DraftsRepository> { DraftsRepositoryImpl(get()) }
    single<RecentGeneratedRepository> { RecentGeneratedRepositoryImpl(get()) }
    single<GuestRepository> { GuestRepositoryImpl(get()) }
    single<GenerateRoomRepository> { GenerateRoomRepositoryImpl(get()) }
}