package thanhdoan.patientservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import thanhdoan.patientservice.dto.PatientRequestDTO;
import thanhdoan.patientservice.dto.PatientResponseDTO;
import thanhdoan.patientservice.exception.EmailAlreadyExistsException;
import thanhdoan.patientservice.exception.PatientNotFoundException;
import thanhdoan.patientservice.mapper.PatientMapper;
import thanhdoan.patientservice.model.Patient;
import thanhdoan.patientservice.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
//        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
//                .map(patient -> PatientMapper.toDTO(patient)).toList();
        return patients.stream().map(PatientMapper::toDTO).toList();
    }


    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(patientRequestDTO.getEmail() + " has been already existed");
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID patientId, PatientRequestDTO patientRequestDTO) {
        Patient existingPatient = patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), patientId)) {
            throw new EmailAlreadyExistsException(patientRequestDTO.getEmail() + " has been already existed");
        }
        existingPatient.setName(patientRequestDTO.getName());
        existingPatient.setAddress(patientRequestDTO.getAddress());
        existingPatient.setEmail(patientRequestDTO.getEmail());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(existingPatient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID patientId) {
        patientRepository.deleteById(patientId);
    }

}