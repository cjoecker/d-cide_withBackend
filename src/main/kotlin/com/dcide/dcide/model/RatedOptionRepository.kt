package com.dcide.dcide.model

import org.springframework.data.jpa.repository.JpaRepository

interface RatedOptionRepository : JpaRepository<RatedOption, Long>
