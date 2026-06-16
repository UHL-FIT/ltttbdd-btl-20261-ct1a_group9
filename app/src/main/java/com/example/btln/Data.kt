package com.example.btln

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path



// PHẦN 2: CÁC LỚP DỮ LIỆU
data class PokemonResult(val name: String, val url: String)
data class PokemonListResponse(val results: List<PokemonResult>)
data class NamedResource(val name: String, val url: String)

data class PokemonDetailResponse(
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val abilities: List<AbilitySlot>,
    val stats: List<StatSlot>,
    val moves: List<MoveSlot>,
    val species: NamedResource
)
data class TypeSlot(val type: NamedResource)
data class AbilitySlot(val ability: NamedResource, val is_hidden: Boolean)
data class StatSlot(val base_stat: Int, val stat: NamedResource)
data class MoveSlot(val move: NamedResource)

data class TypeDetailResponse(val damage_relations: DamageRelations, val pokemon: List<TypePokemonSlot>)
data class TypePokemonSlot(val pokemon: NamedResource)
data class DamageRelations(
    val double_damage_from: List<NamedResource>,
    val double_damage_to: List<NamedResource>,
    val half_damage_from: List<NamedResource>,
    val half_damage_to: List<NamedResource>,
    val no_damage_from: List<NamedResource>,
    val no_damage_to: List<NamedResource>
)

data class GenerationListResponse(val results: List<NamedResource>)
data class GenerationDetailResponse(val main_region: NamedResource, val pokemon_species: List<NamedResource>)

data class PokemonSpeciesResponse(val evolution_chain: EvolutionChainUrl)
data class EvolutionChainUrl(val url: String)
data class EvolutionChainResponse(val chain: ChainLink)
data class ChainLink(val species: NamedResource, val evolves_to: List<ChainLink>)

// PHẦN 3: KẾT NỐI MẠNG
interface PokemonApiService {
    @GET("pokemon?limit=1300")
    suspend fun getPokemonList(): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailResponse

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpeciesResponse

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChainResponse

    @GET("type")
    suspend fun getAllTypes(): TypeListResponse

    @GET("type/{name}")
    suspend fun getTypeDetail(@Path("name") name: String): TypeDetailResponse

    @GET("generation")
    suspend fun getAllGenerations(): GenerationListResponse

    @GET("generation/{id}")
    suspend fun getGenerationDetail(@Path("id") id: Int): GenerationDetailResponse
}

data class TypeListResponse(val results: List<NamedResource>)

object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    val service: PokemonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }
}